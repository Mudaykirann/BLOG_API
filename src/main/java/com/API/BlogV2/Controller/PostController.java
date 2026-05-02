package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.PostDTO;
import com.API.BlogV2.DTO.PostRequestDTO;
import com.API.BlogV2.DTO.PostResponseDTO;
import com.API.BlogV2.Entity.CategoryType;
import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Exception.UnifiedResponse;
import com.API.BlogV2.Service.PostService;
import jakarta.validation.Valid;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;


@RestController
@RequestMapping(path = "api/v1")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(path = "/posts")
    public ResponseEntity<UnifiedResponse<Page<PostResponseDTO>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<PostResponseDTO> allPosts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(UnifiedResponse.ok("Post retrieved", allPosts));
    }


    @GetMapping(path= "posts/{post_id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long post_id) {
        return ResponseEntity.ok(postService.getPostById(post_id));
    }

    @GetMapping(path = "/users/{userId}/posts")
    public ResponseEntity<UnifiedResponse<Page<PostResponseDTO>>> getAllPostsByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<PostResponseDTO> posts = postService.getPostsByUserId(userId, page, size);
        return ResponseEntity.ok(UnifiedResponse.ok("Posts fetched successfully", posts));
    }

    @PostMapping(path = "/users/{userId}/posts")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
    public ResponseEntity<UnifiedResponse<Void>> addNewPost(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody PostRequestDTO p
    ) {
        postService.addNewPost(userId, p);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UnifiedResponse.ok("Post created successfully", null));
    }

    @PutMapping(path="/posts/{postid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnifiedResponse<Void>> updatePost(
            @PathVariable("postid") Long id,
            @RequestBody PostRequestDTO p
    ) throws AccessDeniedException {
        postService.updatePost(id, p);
        return ResponseEntity.ok(UnifiedResponse.ok( "Post Updated successfully", null));
    }

    @DeleteMapping(path = "/posts/{postid}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UnifiedResponse<Void>> deletePost(@PathVariable("postid") Long id) {
        // Note: It's safer to handle ownership logic inside postService.deletePost(id)
        // using SecurityContextHolder like you did in updatePost.
        postService.deletePost(id);
        return ResponseEntity.ok(UnifiedResponse.ok( "Post Deleted successfully", null));
    }


    @GetMapping(path = "/posts/search/{keyword}")
    public ResponseEntity<UnifiedResponse<List<PostResponseDTO>>> searchPosts(@PathVariable("keyword") String keyword){

        List<PostResponseDTO> posts = postService.searchPostByTitle(keyword);
        return ResponseEntity.ok(UnifiedResponse.ok( "Post Deleted successfully", posts));
    }

    @GetMapping(path = "/posts/category/{category}")
    public  ResponseEntity<UnifiedResponse<List<PostResponseDTO>>> getPostsByCategory(@PathVariable("category") String category){
        List<PostResponseDTO> categoryPosts = postService.getPostsByCategory(category);
        return ResponseEntity.ok(UnifiedResponse.ok( "Post Deleted successfully", categoryPosts));
    }

}
