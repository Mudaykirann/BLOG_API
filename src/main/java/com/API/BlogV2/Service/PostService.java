package com.API.BlogV2.Service;

import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Repository.PostRepository;
import com.API.BlogV2.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }


    public void addNewPostWithUser(Long userId, Post p) {
        // Standard JpaRepository uses findById
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User " + userId + " does not exist"));

        p.setUser(user); // Now passing the User object, not the Optional
        postRepository.save(p);
    }

    public Page<Post> getPostsByUserId(Long userId,int page, int size) {

        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());

        // Note: findAllById(userId) is for finding multiple Posts by Post IDs.
        // You should create a custom method in PostRepository: findByUserId(Long userId)
        return postRepository.findByUserId(userId,pageable);
    }

    //    public void deletePost(Long id){}
//    public void updatePost(Long id , Post p){}
}
