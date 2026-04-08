package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.PostDTO;
import com.API.BlogV2.DTO.PostRequestDTO;
import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "api/v1/users/{userId}/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Page<PostDTO> getAllPostsByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return postService.getPostsByUserId(userId,page,size);
    }

    @PostMapping
    public void addNewPost(@PathVariable("userId") Long userId,@Valid @RequestBody PostRequestDTO p) throws IllegalAccessException{
        postService.addNewPostWithUser(userId,p);
    }

//    @PutMapping(path="{postid}")
//    public void updatePost(@PathVariable("postid") Long id , @RequestBody Post p){
//        postService.updatePost(id,p);
//    }
//    @DeleteMapping(path = "{postid}")
//    public void deletePost(@PathVariable("postid") Long id){
//        postService.deletePost(id);
//    }
}
