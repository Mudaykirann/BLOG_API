package com.API.BlogV2.DTO;

import com.API.BlogV2.Entity.CategoryType;
import com.API.BlogV2.Entity.PostStatus;
import com.API.BlogV2.Entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class PostRequestDTO {


    @NotBlank(message="Title is Required.")
    @Size(min = 3, max = 200 , message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "content is required")
    @Size(min=10,message = "Content must be between 10 and 500 characters")
    private String content;

    private Set<CategoryType> categories = new HashSet<>();

    private String coverImageUrl;

    // Publishing state: DRAFT (default), PUBLISHED, or ARCHIVED
    private PostStatus status = PostStatus.DRAFT;

    // Free-form tags (e.g., ["java", "springboot"])
    private Set<String> tags = new HashSet<>();

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Set<CategoryType> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryType> categories) {
        this.categories = categories; // Change from .addAll to direct assignment
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
