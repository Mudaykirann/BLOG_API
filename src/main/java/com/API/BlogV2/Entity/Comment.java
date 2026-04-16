package com.API.BlogV2.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name="comments")
public class Comment {

    @Id
    @SequenceGenerator(
            name = "comment_sequence",
            sequenceName = "comment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_sequence")
    private Long id;

    @Size(min=3,max = 100)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    @JsonIgnore
    private User user;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    @JsonIgnore
    private Post post;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    public Comment(){}
    public Comment( String content, User user, Post post) {
        this.content = content;
        this.user = user;
        this.post = post;
    }


    @JsonProperty("user_id")
    public Long getUserId(){
        return (user!=null)? user.getId() : null;
    }

    @JsonProperty("post_id")
    public Long getPostId() {
        return (post != null) ? post.getId() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
