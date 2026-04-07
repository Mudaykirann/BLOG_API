package com.API.BlogV2.DTO;


import com.API.BlogV2.Entity.Post;
import org.springframework.stereotype.Service;

@Service
public class PostMapper {

    public PostDTO mapToDTO (Post p){
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle(p.getTitle());
        postDTO.setAuthor(p.getAuthor());

        return postDTO;
    }

}
