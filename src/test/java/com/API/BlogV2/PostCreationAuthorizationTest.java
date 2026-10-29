package com.API.BlogV2;

import com.API.BlogV2.Controller.PostController;
import com.API.BlogV2.DTO.PostRequestDTO;
import com.API.BlogV2.Entity.Role;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Entity.UserPrincple;
import com.API.BlogV2.Service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostCreationAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    private User userA;
    private User userB;
    private User adminUser;
    private PostRequestDTO validPostRequest;

    @BeforeEach
    void setUp() {
        // User A (ID = 101)
        userA = new User();
        userA.setId(101L);
        userA.setName("User A");
        userA.setEmail("usera@example.com");
        userA.setPassword("encoded_pwd");
        userA.setRole(Role.USER);

        // User B (ID = 202)
        userB = new User();
        userB.setId(202L);
        userB.setName("User B");
        userB.setEmail("userb@example.com");
        userB.setPassword("encoded_pwd");
        userB.setRole(Role.USER);

        // Admin User (ID = 999)
        adminUser = new User();
        adminUser.setId(999L);
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("encoded_pwd");
        adminUser.setRole(Role.ADMIN);

        // Sample valid post request payload
        validPostRequest = new PostRequestDTO();
        validPostRequest.setTitle("Mastering Spring Boot Security");
        validPostRequest.setContent("This is a detailed post about IDOR mitigation in Spring Boot 3.");
    }

    private UsernamePasswordAuthenticationToken createAuthToken(User user) {
        UserPrincple principal = new UserPrincple(user);
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    @DisplayName("User A CANNOT create post as User B (IDOR prevention) -> Expects 403 Forbidden")
    void testUserCannotCreatePostAsAnotherUser() throws Exception {
        UsernamePasswordAuthenticationToken authA = createAuthToken(userA);

        // User A (101) attempts to submit a post under User B's endpoint (/api/v1/users/202/posts)
        mockMvc.perform(post("/api/v1/users/202/posts")
                        .with(authentication(authA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isForbidden());

        // Verify service was never called with the forged user ID
        verify(postService, never()).addNewPost(eq(202L), any(PostRequestDTO.class));
    }

    @Test
    @DisplayName("User A CAN create a post under their own ID -> Expects 201 Created")
    void testUserCanCreatePostForThemselves() throws Exception {
        UsernamePasswordAuthenticationToken authA = createAuthToken(userA);

        // User A (101) submits a post under their own endpoint (/api/v1/users/101/posts)
        mockMvc.perform(post("/api/v1/users/101/posts")
                        .with(authentication(authA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isCreated());

        // Verify service was invoked for User A
        verify(postService, times(1)).addNewPost(eq(101L), any(PostRequestDTO.class));
    }

    @Test
    @DisplayName("Admin CAN create a post for any user -> Expects 201 Created")
    void testAdminCanCreatePostForAnyUser() throws Exception {
        UsernamePasswordAuthenticationToken adminAuth = createAuthToken(adminUser);

        // Admin submits a post under User B's endpoint (/api/v1/users/202/posts)
        mockMvc.perform(post("/api/v1/users/202/posts")
                        .with(authentication(adminAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isCreated());

        // Verify service was invoked
        verify(postService, times(1)).addNewPost(eq(202L), any(PostRequestDTO.class));
    }

    @Test
    @DisplayName("Unauthenticated request cannot create posts -> Expects 401/403")
    void testUnauthenticatedCannotCreatePost() throws Exception {
        mockMvc.perform(post("/api/v1/users/101/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPostRequest)))
                .andExpect(status().isForbidden());

        verify(postService, never()).addNewPost(any(), any());
    }
}
