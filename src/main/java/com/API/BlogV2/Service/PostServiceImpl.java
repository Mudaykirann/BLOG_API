package com.API.BlogV2.Service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.API.BlogV2.Exception.ResourceNotFoundException;
import com.API.BlogV2.Exception.BlogAPIException;
import com.API.BlogV2.DTO.*;
import com.API.BlogV2.Entity.CategoryType;
import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Entity.UserPrincple;
import com.API.BlogV2.Repository.PostRepository;
import com.API.BlogV2.Repository.UserRepository;
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
    private final ImageKitService imageKitService; // inject this
    private final PostMapper postMapper;





    @Caching(evict = {
            @CacheEvict(value = "allPosts", allEntries = true),
            @CacheEvict(value = "userPosts", allEntries = true)
    })
    public void addNewPost(Long userId, PostRequestDTO postRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Post p = new Post();
        p.setTitle(postRequestDTO.getTitle());
        p.setContent(postRequestDTO.getContent());
        p.setUser(user);
        p.setCoverImageUrl(postRequestDTO.getCoverImageUrl());


        // DEBUG: Print here to see if the DTO is actually receiving data from Postman
        log.info("Categories from DTO: {}", postRequestDTO.getCategories());

        if(postRequestDTO.getCategories() != null){
            p.getCategories().addAll(postRequestDTO.getCategories());
        }

        Post savedPost = postRepository.save(p);
        log.info("Saved Post Categories: {}", savedPost.getCategories());
    }


    @Cacheable(value = "userPosts", key = "#userId + '_' + #page + '_' + #size")
    @Transactional()  // ensures the session stays open to fetch lazy-loaded comments
    public PageResponseDTO<PostResponseDTO> getPostsByUserId(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());

        Page<Post> postPage = postRepository.findByUserId(userId,pageable);

        // this is where DTO comes into picture , observer the mapToDTO
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

        Page<PostResponseDTO> mappedPage = postRepository.findAll(pageable)
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
            @CacheEvict(value = "postsByCategory", allEntries = true)
    })
    @Transactional
    public void updatePost(Long id, PostRequestDTO dto) throws AccessDeniedException {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        UserPrincple userDetails = (UserPrincple) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Long currentUserId = userDetails.getId();

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!post.getUser().getId().equals(currentUserId) && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to edit this post");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        if (dto.getCategories() != null) {
            post.getCategories().clear();
            post.getCategories().addAll(dto.getCategories());
        }

        postRepository.save(post);
    }
    @Caching(evict = {
            @CacheEvict(value = "post", key = "#id"),
            @CacheEvict(value = "allPosts", allEntries = true),
            @CacheEvict(value = "userPosts", allEntries = true),
            @CacheEvict(value = "postSearch", allEntries = true),
            @CacheEvict(value = "postsByCategory", allEntries = true)
    })
    public void deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        postRepository.delete(post);
    }

    @Cacheable(value = "post", key = "#id")
    @Transactional()
    public PostResponseDTO getPostById(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        return postMapper.mapToDTO(post);
    }


    @Cacheable(value = "postSearch", key = "#keyword")
    @Transactional()
    public List<PostResponseDTO> searchPostByTitle(String keyword) {

        return postRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(postMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    // Filter posts by category
    @Cacheable(value = "postsByCategory", key = "#categoryName")
    @Transactional()
    public List<PostResponseDTO> getPostsByCategory(String categoryName) {

        CategoryType category = CategoryType.valueOf(categoryName.toUpperCase());

        return postRepository.findByCategoriesContaining(category)
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
