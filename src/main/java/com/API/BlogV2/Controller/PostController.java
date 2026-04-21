package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.PostDTO;
import com.API.BlogV2.DTO.PostRequestDTO;
import com.API.BlogV2.Exception.UnifiedResponse;
import com.API.BlogV2.Service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;


@RestController
@RequestMapping(path = "api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<UnifiedResponse<Page<PostDTO>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<PostDTO> allPosts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(UnifiedResponse.ok("Post retrieved", allPosts));
    }

    @GetMapping(path = "{userId}/all")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UnifiedResponse<Page<PostDTO>>> getAllPostsByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<PostDTO> posts = postService.getPostsByUserId(userId, page, size);
        return ResponseEntity.ok(UnifiedResponse.ok("Posts fetched successfully", posts));
    }

    @PostMapping(path = "/{userId}/new")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
    public ResponseEntity<UnifiedResponse<Void>> addNewPost(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody PostRequestDTO p
    ) {
        postService.addNewPostWithUser(userId, p);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UnifiedResponse.ok("Post created successfully", null));
    }

    @PutMapping(path="/{postid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnifiedResponse<Void>> updatePost(
            @PathVariable("postid") Long id,
            @RequestBody PostRequestDTO p
    ) throws AccessDeniedException {
        postService.updatePost(id, p);
        return ResponseEntity.ok(UnifiedResponse.ok( "Post Updated successfully", null));
    }

    @DeleteMapping(path = "/{postid}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UnifiedResponse<Void>> deletePost(@PathVariable("postid") Long id) {
        // Note: It's safer to handle ownership logic inside postService.deletePost(id)
        // using SecurityContextHolder like you did in updatePost.
        postService.deletePost(id);
        return ResponseEntity.ok(UnifiedResponse.ok( "Post Deleted successfully", null));
    }

}
