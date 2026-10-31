package com.API.BlogV2.Controller;


import com.API.BlogV2.DTO.CommentDTO;
import com.API.BlogV2.DTO.CommentRequestDTO;
import com.API.BlogV2.Entity.Comment;
import com.API.BlogV2.Entity.UserPrincple;
import com.API.BlogV2.Exception.UnifiedResponse;
import com.API.BlogV2.Service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1")
public class CommentController {

    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //get comments
    @GetMapping(path = "/posts/{postId}/comments")
    public ResponseEntity<UnifiedResponse<List<CommentDTO>>> getCommentsByPostId(
            @PathVariable("postId") Long postId
    ){
        List<CommentDTO> comments  = commentService.getAllComments(postId);
        return ResponseEntity.ok(UnifiedResponse.ok( "Comments Fetched successfully", comments));
    }


    //create comments
    @PostMapping(path = "/posts/{postId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnifiedResponse<Void>> addNewComment(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CommentRequestDTO request
    ){
        UserPrincple principal = (UserPrincple) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        commentService.addComment(postId, principal.getId(), request.getContent());
        return ResponseEntity.ok(UnifiedResponse.ok( "Comment Added successfully", null));
    }


    @PutMapping(path = "posts/{postId}/comments/{comment_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnifiedResponse<Void>> updateComment(
            @PathVariable("postId") Long postId,
            @PathVariable("comment_id") Long comment_id,
            @Valid @RequestBody CommentRequestDTO request
    ){
        UserPrincple principal = (UserPrincple) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        commentService.updateComment(postId, principal.getId(), comment_id, request.getContent());

        return ResponseEntity.ok(UnifiedResponse.ok( "Comment updated successfully", null));
    }

    @DeleteMapping(path = "/posts/{postId}/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnifiedResponse<Void>> deleteComment(
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId
    ){

        UserPrincple principal = (UserPrincple) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        commentService.deleteComment(postId,principal.getId(),commentId);
        return ResponseEntity.ok(UnifiedResponse.ok( "Comment Deleted successfully", null));
    }

}
