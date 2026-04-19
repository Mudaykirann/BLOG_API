package com.API.BlogV2.DTO;


import com.API.BlogV2.Entity.Post;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostMapper {

    public PostDTO mapToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(post.getAuthor());

        if (post.getComments() != null) {
            List<CommentDTO> commentDtos = post.getComments().stream()
                    .map(comment -> new CommentDTO(
                            comment.getContent(),
                            comment.getUser().getId(),
                            comment.getPost().getId()
                    ))
                    .collect(Collectors.toList());
            dto.setComments(commentDtos);
        }
        return dto;
    }

    public Post mapToEntity (PostDTO p){

        Post post = new Post();
        post.setTitle(p.getTitle());
        post.setAuthor(p.getAuthor());
        post.setContent(p.getContent());

        return post;
    }

}
