package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.PostDTO;
import com.API.BlogV2.DTO.PostRequestDTO;
import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Exception.ApiResponse;
import com.API.BlogV2.Service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;


@RestController
@RequestMapping(path = "api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping(path = "/all")
    public ApiResponse<Page<PostDTO>> getAllPosts(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<PostDTO> allPosts = postService.getAllPosts(page,size);
        return  new ApiResponse<>("success","All Posts are Fetched successfully",allPosts);
    }

    @GetMapping(path = "{userId}/all")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<Page<PostDTO>> getAllPostsByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<PostDTO> posts = postService.getPostsByUserId(userId,page,size);
        return new ApiResponse<>("success","Posts fetched successfully",posts);
    }

    @PostMapping(path = "/{userId}/new")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
    public ApiResponse<Void> addNewPost(@PathVariable("userId") Long userId,@Valid @RequestBody PostRequestDTO p) throws IllegalAccessException{
        postService.addNewPostWithUser(userId,p);
        return new ApiResponse<>("success", "Post created successfully", null);
    }

    @PutMapping(path="/{postid}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> updatePost(@PathVariable("postid") Long id, @RequestBody PostRequestDTO p) throws AccessDeniedException {
        postService.updatePost(id, p);
        return new ApiResponse<>("success", "Post Updated successfully", null);
    }


    @DeleteMapping(path = "/{postid}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
    public ApiResponse<Void> deletePost(@PathVariable("postid") Long id){
        postService.deletePost(id);
        return new ApiResponse<>("success", "Post Deleted successfully", null);
    }
}
