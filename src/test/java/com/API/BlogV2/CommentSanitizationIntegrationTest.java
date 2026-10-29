package com.API.BlogV2;

import com.API.BlogV2.DTO.CommentRequestDTO;
import com.API.BlogV2.Entity.Comment;
import com.API.BlogV2.Entity.Post;
import com.API.BlogV2.Entity.Role;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Entity.UserPrincple;
import com.API.BlogV2.Repository.CommentRepository;
import com.API.BlogV2.Repository.PostRepository;
import com.API.BlogV2.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CommentSanitizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CommentRepository commentRepository;

    private User authUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        authUser = new User();
        authUser.setId(10L);
        authUser.setName("Alice");
        authUser.setEmail("alice@example.com");
        authUser.setPassword("encoded_pwd");
        authUser.setRole(Role.USER);

        testPost = new Post();
        testPost.setId(100L);
        testPost.setTitle("Test Post");
        testPost.setCommentCount(0);
        testPost.setUser(authUser);
    }

    private UsernamePasswordAuthenticationToken createAuthToken(User user) {
        UserPrincple principal = new UserPrincple(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    @DisplayName("HTML and script tags in comments are stripped before persistence")
    void testXssStrippedInComment() throws Exception {
        when(postRepository.findById(100L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(10L)).thenReturn(Optional.of(authUser));

        CommentRequestDTO request = new CommentRequestDTO("Nice post! <script>alert('xss')</script>");

        mockMvc.perform(post("/api/v1/posts/100/comments")
                        .with(authentication(createAuthToken(authUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comment Added successfully"));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        Comment savedComment = captor.getValue();
        assertEquals("Nice post!", savedComment.getContent());
        assertFalse(savedComment.getContent().contains("<script>"));
    }

    @Test
    @DisplayName("Comment consisting only of malicious tags fails validation after sanitization")
    void testOnlyTagsFailsSanitizationValidation() throws Exception {
        when(postRepository.findById(100L)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(10L)).thenReturn(Optional.of(authUser));

        // When stripped, content is empty (""), which is < 3 characters
        CommentRequestDTO request = new CommentRequestDTO("<script>alert('xss')</script>");

        mockMvc.perform(post("/api/v1/posts/100/comments")
                        .with(authentication(createAuthToken(authUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Comment under 3 characters fails validation (HTTP 400)")
    void testCommentTooShortFails() throws Exception {
        CommentRequestDTO request = new CommentRequestDTO("Hi");

        mockMvc.perform(post("/api/v1/posts/100/comments")
                        .with(authentication(createAuthToken(authUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Comment exceeding 100 characters fails validation (HTTP 400)")
    void testCommentTooLongFails() throws Exception {
        String longContent = "A".repeat(101);
        CommentRequestDTO request = new CommentRequestDTO(longContent);

        mockMvc.perform(post("/api/v1/posts/100/comments")
                        .with(authentication(createAuthToken(authUser)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(commentRepository, never()).save(any());
    }
}
