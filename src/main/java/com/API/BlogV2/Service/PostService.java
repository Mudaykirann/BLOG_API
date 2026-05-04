package com.API.BlogV2.Service;

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

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageKitService imageKitService; // inject this

    @Autowired
    private PostMapper postMapper;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, ImageKitService imageKitService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageKitService = imageKitService;
    }




    public void addNewPost(Long userId, PostRequestDTO postRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Post p = new Post();
        p.setTitle(postRequestDTO.getTitle());
        p.setContent(postRequestDTO.getContent());
        p.setUser(user);
        p.setCoverImageUrl(postRequestDTO.getCoverImageUrl());


        // DEBUG: Print here to see if the DTO is actually receiving data from Postman
        System.out.println("Categories from DTO: " + postRequestDTO.getCategories());

        if(postRequestDTO.getCategories() != null){
            p.getCategories().addAll(postRequestDTO.getCategories());
        }

        Post savedPost = postRepository.save(p);
        System.out.println("Saved Post Categories: " + savedPost.getCategories());
    }


    @Transactional  // ensures the session stays open to fetch lazy-loaded comments
    public Page<PostResponseDTO> getPostsByUserId(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());

        Page<Post> postPage = postRepository.findByUserId(userId,pageable);

        // this is where DTO comes into picture , observer the mapToDTO
        return postPage.map(postMapper::mapToDTO);
    }


    @Transactional
    public Page<PostResponseDTO> getAllPosts(int page, int size) {


        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return postRepository.findAll(pageable)
                .map(postMapper::mapToDTO);
    }

    @Transactional
    public void updatePost(Long id, PostRequestDTO dto) throws AccessDeniedException {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

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
    public void deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));

        postRepository.delete(post);
    }

    @Transactional
    public PostResponseDTO getPostById(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return postMapper.mapToDTO(post);
    }


    public List<PostResponseDTO> searchPostByTitle(String keyword) {

        return postRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(postMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    // Filter posts by category
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
    public Post updateCoverImage(Long postId, String imageUrl) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

        post.setCoverImageUrl(imageUrl);
        return postRepository.save(post);
    }

    // Optional: get a resized thumbnail of the post cover
    public String getCoverThumbnail(Long postId, int width, int height) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

        return imageKitService.getTransformedUrl(post.getCoverImageUrl(), width, height);
    }
}
