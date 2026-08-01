package com.API.BlogV2.DTO;

import java.util.Date;
import java.util.List;
import java.util.Set;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailDTO implements Serializable {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String authorName;
    private String authorBio;
    private String coverImageUrl;
    private Set<String> tags;
    private List<CommentDTO> comments;
    private Date createdAt;
    private Date updatedAt;
    private int viewCount;
    private int likeCount;
}
