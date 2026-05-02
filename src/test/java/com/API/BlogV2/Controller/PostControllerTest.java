package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.PostDTO;
import com.API.BlogV2.DTO.PostRequestDTO;
import com.API.BlogV2.Service.JWTService;
import com.API.BlogV2.Service.PostService;
import com.API.BlogV2.Utils.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;


    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private JwtFilter jwtFilter;

    private final int page=0;
    private final int size=2;
//    @Test
//    void getAllPosts() throws Exception {
//
//
//        Page<PostDTO> posts =new PageImpl<>(List.of(new PostDTO()));
//
//        when(postService.getAllPosts(page,size)).thenReturn(posts);
//        mockMvc.perform(get("/api/posts/all"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("success"));
//    }
//
//    @Test
//    @WithMockUser(roles = "USER")
//    void getAllPostsByUser() throws Exception {
//        long userId=1;
//        Page<PostDTO> posts = new PageImpl<>(List.of(new PostDTO()));
//
//        when(postService.getPostsByUserId(userId,page,size)).thenReturn(posts);
//        mockMvc.perform(get("/api/posts/{userId}/all",userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("success"));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void addNewPost() throws Exception {
//
//        long userId = 1;
//
//        doNothing().when(postService)
//                .addNewPostWithUser(any(Long.class), any(PostRequestDTO.class));
//
//        mockMvc.perform(post("/api/posts/{userId}/new", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding("utf-8")
//                        .content("""
//                {
//                    "title": "Blog Post",
//                    "author": "John Doe",
//                    "content": "This is a valid blog content"
//                }
//            """))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message")
//                        .value("Post created successfully"))
//                .andExpect(jsonPath("$.status")
//                        .value("success"));
//    }
//}