package com.API.BlogV2.Service;

import com.API.BlogV2.DTO.CommentDTO;

import java.util.List;

public interface CommentService {
    List<CommentDTO> getAllComments(Long post_id);
    void addComment(Long postId, Long userId, String content);
    void updateComment(Long postId, Long userId, Long commentId, String newContent);
    void deleteComment(Long postId, Long userId, Long commentId);
}
