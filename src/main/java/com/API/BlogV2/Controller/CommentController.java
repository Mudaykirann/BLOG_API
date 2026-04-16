package com.API.BlogV2.Controller;


import com.API.BlogV2.Entity.Comment;
import com.API.BlogV2.Entity.UserPrincple;
import com.API.BlogV2.Exception.ApiResponse;
import com.API.BlogV2.Repository.CommentRepository;
import com.API.BlogV2.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/posts")
public class CommentController {

    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping(path = "/{post_id}/comments")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ApiResponse<List<Comment>> getCommentsByPostId(
            @PathVariable("post_id") Long post_id
    ){
        List<Comment> comments  = commentService.getAllComments(post_id);
        return new ApiResponse<>("success", "Comments Fetched successfully", comments);
    }

    @PostMapping(path = "/{post_id}/comments")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')") // Simple check; ID is handled inside
    public ApiResponse<Void> addNewComment(
            @PathVariable("post_id") Long post_id,
            @RequestBody Map<String ,String> requestBody
    ){
        UserPrincple principal = (UserPrincple) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String content = requestBody.get("content");
        commentService.addComment(post_id, principal.getId(), content);
        return new ApiResponse<>("success", "Comment Added successfully", null);
    }

}
