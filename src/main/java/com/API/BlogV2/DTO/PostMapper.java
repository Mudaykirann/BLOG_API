package com.API.BlogV2.DTO;

import com.API.BlogV2.Entity.Comment;
import com.API.BlogV2.Entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@Mapper(componentModel = "spring") // This makes it a Spring Bean (@Component)
public interface PostMapper {

    // 1. Map Entity to DTO
    @Mapping(target = "authorName", source = "user.name")
    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "comments", source = "comments")
    PostResponseDTO mapToDTO(Post post);

    // 2. Map DTO back to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post mapToEntity(PostDTO postDTO);

    // 3. Helper to map individual Comments within the list
    @Mapping(target = "userId", source = "user.id") //source is for telling the bean to go deep into the user to get the id
    @Mapping(target = "postId", source = "post.id")
    CommentDTO commentToCommentDTO(Comment comment);
}