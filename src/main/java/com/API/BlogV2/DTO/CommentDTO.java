package com.API.BlogV2.DTO;

import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Entity.User;

public class CommentDTO {
    private String content;
    private Long userId;
    private Long postId;

    // Default constructor for Jackson
    public CommentDTO() {}

    // Constructor used in your Service/Mapper logic
    public CommentDTO(String content, Long userId, Long postId) {
        this.content = content;
        this.userId = userId;
        this.postId = postId;
    }

    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
}
