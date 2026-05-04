package com.API.BlogV2.DTO;

import com.API.BlogV2.Entity.CategoryType;
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
    @Size(min=10,max=500,message = "Content must be between 10 and 500 characters")
    private String content;

    private Set<CategoryType> categories = new HashSet<>();

    private String coverImageUrl;

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
}
