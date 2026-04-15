package com.API.BlogV2.Service;

import com.API.BlogV2.DTO.PostDTO;
import com.API.BlogV2.DTO.PostMapper;
import com.API.BlogV2.DTO.PostRequestDTO;
import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Entity.UserPrincple;
import com.API.BlogV2.Repository.PostRepository;
import com.API.BlogV2.Repository.UserRepository;
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


@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }


    public void addNewPostWithUser(Long userId, PostRequestDTO postRequestDTO) {
        // Standard JpaRepository uses findById
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User " + userId + " does not exist"));

        Post p = new Post();
        p.setTitle(postRequestDTO.getTitle());
        p.setAuthor(postRequestDTO.getAuthor());
        p.setContent(postRequestDTO.getContent());
        p.setUser(user); // Now passing the User object, not the Optional
        postRepository.save(p);
    }

    public Page<PostDTO> getPostsByUserId(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());

        Page<Post> postPage = postRepository.findByUserId(userId,pageable);

        // this is where DTO comes into picture , observer the mapToDTO
        return postPage.map(post -> postMapper.mapToDTO(post));
    }


    public Page<PostDTO> getAllPosts(int page, int size) {

        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());

        //Page<Post> postPage = postRepository.findByUserId(userId,pageable);
        Page<Post> postPage = postRepository.findAll(pageable);

        // this is where DTO comes into picture , observer the mapToDTO
        return postPage.map(post -> postMapper.mapToDTO(post));
    }

    public void updatePost(Long id , PostRequestDTO p) throws AccessDeniedException {
        // 1. Fetch the post (The "Real" Entity)
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 2. Get the currently logged-in user's ID
        // Note: Casting depends on how your CustomUserDetails is structured
        UserPrincple userDetails = (UserPrincple) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long currentUserId = userDetails != null ? userDetails.getId() : null;

        // 3. The Match: Check Ownership or Admin status
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!post.getUser().getId().equals(currentUserId) && !isAdmin) {
            throw new AccessDeniedException("You do not have permission to edit this post");
        }

        // 4. If we got here, they are allowed! Update the fields
        post.setTitle(p.getTitle());
        post.setAuthor(p.getAuthor());
        post.setContent(p.getContent());

        postRepository.save(post);
    }
    public void deletePost(Long id){
        Post p = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found"));
        postRepository.delete(p);
    }
}
