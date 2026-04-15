package com.API.BlogV2.DTO;

import com.API.BlogV2.Entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequestDTO {


    @NotBlank(message="Title is Required.")
    @Size(min = 3, max = 200 , message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Author is required.")
    private String author;


    @NotBlank(message = "content is required")
    @Size(min=10,max=500,message = "Content must be between 10 and 500 characters")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
