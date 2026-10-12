package com.API.BlogV2.DTO;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.API.BlogV2.Entity.CategoryType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryDTO implements Serializable {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String authorName;
    private String coverImageUrl;
    private Date publishedAt;
    private int commentCount;
    private int likeCount;
    private Set<CategoryType> categories;
    private Set<String> tags;
    private int readTime;
}
