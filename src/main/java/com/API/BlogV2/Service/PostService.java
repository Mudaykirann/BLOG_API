package com.API.BlogV2.Service;

import com.API.BlogV2.DTO.PostDTO;
import com.API.BlogV2.DTO.PostMapper;
import com.API.BlogV2.DTO.PostRequestDTO;
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

    //    public void deletePost(Long id){}
//    public void updatePost(Long id , Post p){}
}
