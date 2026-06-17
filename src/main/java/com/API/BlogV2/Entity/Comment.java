package com.API.BlogV2.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name="comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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




    @JsonProperty("user_id")
    public Long getUserId(){
        return (user!=null)? user.getId() : null;
    }

    @JsonProperty("post_id")
    public Long getPostId() {
        return (post != null) ? post.getId() : null;
    }


}
