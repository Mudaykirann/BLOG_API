package com.API.BlogV2.DTO;




//flow is like this
//Controller → Service → Repository
//        ↓
//        Entity
//        ↓
//        Mapper
//        ↓
//        DTO

import com.API.BlogV2.Entity.Comment;
import com.API.BlogV2.Entity.Post;

import java.util.ArrayList;
import java.util.List;

public class PostDTO {

    private String title;
    private String author;
    private String content;

    private List<Comment> comments = new ArrayList<>();


    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
