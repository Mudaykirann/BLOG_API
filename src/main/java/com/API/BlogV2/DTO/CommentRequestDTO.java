package com.API.BlogV2.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequestDTO {

    @NotBlank(message = "Comment content cannot be blank")
    @Size(min = 3, max = 100, message = "Comment must be between 3 and 100 characters")
    private String content;

    public CommentRequestDTO() {}

    public CommentRequestDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
