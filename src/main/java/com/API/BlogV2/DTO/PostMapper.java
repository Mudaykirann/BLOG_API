package com.API.BlogV2.DTO;


import com.API.BlogV2.Entity.Post;
import org.springframework.stereotype.Service;

@Service
public class PostMapper {

    public PostDTO mapToDTO (Post p){
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle(p.getTitle());
        postDTO.setAuthor(p.getAuthor());
        postDTO.setContent(p.getContent());

        return postDTO;
    }

    public Post mapToEntity (PostDTO p){

        Post post = new Post();
        post.setTitle(p.getTitle());
        post.setAuthor(p.getAuthor());
        post.setContent(p.getContent());

        return post;
    }

}
