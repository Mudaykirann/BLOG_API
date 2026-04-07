package com.API.BlogV2.DTO;




//flow is like this
//Controller → Service → Repository
//        ↓
//        Entity
//        ↓
//        Mapper
//        ↓
//        DTO

import com.API.BlogV2.Entity.Post;

public class PostDTO {

    private String title;
    private String author;

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
