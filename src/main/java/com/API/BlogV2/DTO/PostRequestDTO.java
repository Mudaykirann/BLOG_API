package com.API.BlogV2.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequestDTO {


    @NotBlank(message="Title is Required.")
    @Size(min = 3, max = 200 , message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Author is required.")
    private String author;




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
