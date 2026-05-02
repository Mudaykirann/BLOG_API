package com.API.BlogV2.DTO;
import com.API.BlogV2.Entity.CategoryType;
import com.API.BlogV2.Entity.Comment;
import com.API.BlogV2.Entity.Post;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class PostDTO {

    private String title;
    private String content;

    private Set<CategoryType> categories = new HashSet<>();

    public Set<CategoryType> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryType> categories) {
        this.categories = categories;
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
