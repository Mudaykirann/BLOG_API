package com.API.BlogV2.DTO;

import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CommentDTO implements Serializable {
    private Long id;
    private String content;
    private Long userId;
    private String authorName;
    private Long postId;
    private LocalDateTime createdAt;

    // Default constructor for Jackson
    public CommentDTO() {}

    // Constructor used in your Service/Mapper logic
    public CommentDTO(String content, Long userId, Long postId) {
        this.content = content;
        this.userId = userId;
        this.postId = postId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
