package com.API.BlogV2.Service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.API.BlogV2.Exception.ResourceNotFoundException;
import com.API.BlogV2.Exception.BlogAPIException;
import com.API.BlogV2.DTO.*;
import com.API.BlogV2.Entity.CategoryType;
import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Entity.PostStatus;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Entity.UserPrincple;
import com.API.BlogV2.Repository.PostRepository;
import com.API.BlogV2.Repository.UserRepository;
import com.API.BlogV2.Utils.HtmlSanitizerUtil;
import com.API.BlogV2.Utils.SlugGenerator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageKitService imageKitService;
    private final PostMapper postMapper;
    private final HtmlSanitizerUtil htmlSanitizerUtil;
    private final SlugGenerator slugGenerator;





    @Caching(evict = {
            @CacheEvict(value = "allPosts", allEntries = true),
            @CacheEvict(value = "userPosts", allEntries = true)
    })
    public void addNewPost(Long userId, PostRequestDTO postRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Post p = new Post();
        p.setTitle(postRequestDTO.getTitle());
        // Sanitize content to strip any XSS before persisting
        p.setContent(htmlSanitizerUtil.sanitize(postRequestDTO.getContent()));
        p.setSlug(slugGenerator.generateUniqueSlug(postRequestDTO.getTitle()));
        p.setUser(user);
        p.setCoverImageUrl(postRequestDTO.getCoverImageUrl());
        // Use the status from the request (DRAFT by default if not provided)
        p.setStatus(postRequestDTO.getStatus() != null ? postRequestDTO.getStatus() : PostStatus.DRAFT);

        log.info("Categories from DTO: {}", postRequestDTO.getCategories());
        if (postRequestDTO.getCategories() != null) {
            p.getCategories().addAll(postRequestDTO.getCategories());
        }

        // Save tags
        if (postRequestDTO.getTags() != null) {
            p.getTags().addAll(postRequestDTO.getTags());
        }

        Post savedPost = postRepository.save(p);
        log.info("Saved Post Categories: {}", savedPost.getCategories());
        log.info("Saved Post Tags: {}", savedPost.getTags());
        log.info("Post created: slug={}", savedPost.getSlug());
    }

    @Override
    @Transactional
    public PageResponseDTO<PostSummaryDTO> getAllPostsSummary(Pageable pageable) {
        Page<PostSummaryDTO> mappedPage = postRepository.findProjectedByStatus(PostStatus.PUBLISHED, pageable);
        return new PageResponseDTO<>(
                mappedPage.getContent(),
                mappedPage.getNumber(),
                mappedPage.getSize(),
                mappedPage.getTotalElements(),
                mappedPage.getTotalPages(),
                mappedPage.isLast()
        );
    }

    @Override
    @Cacheable(value = "postBySlug", key = "#slug")
    @Transactional
    public PostDetailDTO getPostDetailBySlug(String slug) {
        Post post = postRepository.findBySlugWithTagsAndComments(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "slug", slug));

        List<CommentDTO> commentDTOs = post.getComments().stream().map(c -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(c.getId());
            dto.setContent(c.getContent());
            dto.setAuthorName(c.getUser().getDisplayName());
            dto.setCreatedAt(c.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        return new PostDetailDTO(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getContent(),
                post.getUser().getDisplayName(),
                post.getUser().getBio(),
                post.getCoverImageUrl(),
                new java.util.HashSet<>(post.getTags()),
                commentDTOs,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getViewCount(),
                post.getLikeCount()
        );
    }

    @Override
    @org.springframework.scheduling.annotation.Async
    public void incrementViewCountAsync(String slug) {
        postRepository.incrementViewCountBySlug(slug);
    }


    @Cacheable(value = "userPosts", key = "#userId + '_' + #page + '_' + #size")
    @Transactional()
    public PageResponseDTO<PostResponseDTO> getPostsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        // Fetch all statuses for this author so they can manage drafts
        Page<Post> postPage = postRepository.findByUserId(userId, pageable);
        Page<PostResponseDTO> mappedPage = postPage.map(postMapper::mapToDTO);
        return new PageResponseDTO<>(
                mappedPage.getContent(),
                mappedPage.getNumber(),
                mappedPage.getSize(),
                mappedPage.getTotalElements(),
                mappedPage.getTotalPages(),
                mappedPage.isLast()
        );
    }


    @Cacheable(value = "allPosts", key = "#page + '_' + #size")
    @Transactional()
    public PageResponseDTO<PostResponseDTO> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        // Public feed: PUBLISHED posts only
        Page<PostResponseDTO> mappedPage = postRepository
                .findAllByStatus(PostStatus.PUBLISHED, pageable)
                .map(postMapper::mapToDTO);
        return new PageResponseDTO<>(
                mappedPage.getContent(),
                mappedPage.getNumber(),
                mappedPage.getSize(),
                mappedPage.getTotalElements(),
                mappedPage.getTotalPages(),
                mappedPage.isLast()
        );
    }

    @Caching(evict = {
            @CacheEvict(value = "post", key = "#id"),
            @CacheEvict(value = "allPosts", allEntries = true),
            @CacheEvict(value = "userPosts", allEntries = true),
            @CacheEvict(value = "postSearch", allEntries = true),
            @CacheEvict(value = "postsByCategory", allEntries = true),
            @CacheEvict(value = "postBySlug", allEntries = true)
    })
    @Transactional
    public void updatePost(Long id, PostRequestDTO dto) throws AccessDeniedException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        UserPrincple userDetails = (UserPrincple) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long currentUserId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!post.getUser().getId().equals(currentUserId) && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to edit this post");
        }

        post.setTitle(dto.getTitle());
        // Sanitize content on update as well
        post.setContent(htmlSanitizerUtil.sanitize(dto.getContent()));

        if (dto.getCategories() != null) {
            post.getCategories().clear();
            post.getCategories().addAll(dto.getCategories());
        }

        // Update tags
        if (dto.getTags() != null) {
            post.getTags().clear();
            post.getTags().addAll(dto.getTags());
        }

        // Update publishing status if provided
        if (dto.getStatus() != null) {
            post.setStatus(dto.getStatus());
        }

        postRepository.save(post);
        log.info("Post updated: id={}", id);
    }
    @Caching(evict = {
            @CacheEvict(value = "post", key = "#id"),
            @CacheEvict(value = "allPosts", allEntries = true),
            @CacheEvict(value = "userPosts", allEntries = true),
            @CacheEvict(value = "postSearch", allEntries = true),
            @CacheEvict(value = "postsByCategory", allEntries = true),
            @CacheEvict(value = "postBySlug", allEntries = true)
    })
    public void deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        postRepository.delete(post);
        log.info("Post deleted: id={}", id);
    }

    @Cacheable(value = "post", key = "#id")
    @Transactional()
    public PostResponseDTO getPostById(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        return postMapper.mapToDTO(post);
    }


    // Search — only PUBLISHED posts are visible
    @Cacheable(value = "postSearch", key = "#keyword")
    @Transactional()
    public List<PostResponseDTO> searchPostByTitle(String keyword) {
        return postRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, PostStatus.PUBLISHED)
                .stream()
                .map(postMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    // Filter by category — only PUBLISHED
    @Cacheable(value = "postsByCategory", key = "#categoryName")
    @Transactional()
    public List<PostResponseDTO> getPostsByCategory(String categoryName) {
        CategoryType category = CategoryType.valueOf(categoryName.toUpperCase());
        return postRepository.findByCategoryAndStatus(category, PostStatus.PUBLISHED)
                .stream()
                .map(postMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    // Filter by tag — only PUBLISHED
    @Cacheable(value = "postsByTag", key = "#tag")
    @Transactional()
    public List<PostResponseDTO> getPostsByTag(String tag) {
        return postRepository.findByTagAndStatus(tag.toLowerCase(), PostStatus.PUBLISHED)
                .stream()
                .map(postMapper::mapToDTO)
                .collect(Collectors.toList());
    }


    // In your existing PostService.java — ADD this method



    /**
     * Called after frontend uploads the image and gets back a URL from ImageKit.
     */
    @CacheEvict(value = "post", key = "#postId")
    public PostResponseDTO updateCoverImage(Long postId, String imageUrl) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        post.setCoverImageUrl(imageUrl);
        return postMapper.mapToDTO(postRepository.save(post));
    }

    // Optional: get a resized thumbnail of the post cover
    @Transactional()
    public String getCoverThumbnail(Long postId, int width, int height) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        return imageKitService.getTransformedUrl(post.getCoverImageUrl(), width, height);
    }
}
