package com.API.BlogV2.Service;

import com.API.BlogV2.DTO.PostRequestDTO;
import com.API.BlogV2.DTO.PostResponseDTO;
import com.API.BlogV2.Entity.Post;
import org.springframework.data.domain.Page;
import com.API.BlogV2.DTO.PageResponseDTO;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface PostService {
    void addNewPost(Long userId, PostRequestDTO postRequestDTO);
    PageResponseDTO<PostResponseDTO> getPostsByUserId(Long userId, int page, int size);
    PageResponseDTO<PostResponseDTO> getAllPosts(int page, int size);
    void updatePost(Long id, PostRequestDTO dto) throws AccessDeniedException;
    void deletePost(Long id);
    PostResponseDTO getPostById(Long id);
    List<PostResponseDTO> searchPostByTitle(String keyword);
    List<PostResponseDTO> getPostsByCategory(String categoryName);
    PostResponseDTO updateCoverImage(Long postId, String imageUrl);
    String getCoverThumbnail(Long postId, int width, int height);
    List<PostResponseDTO> getPostsByTag(String tag);
}
