package com.API.BlogV2;

import com.API.BlogV2.DTO.LoginDTO;
import com.API.BlogV2.Entity.RefreshToken;
import com.API.BlogV2.Entity.Role;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Repository.UserRepository;
import com.API.BlogV2.Service.RefreshTokenService;
import com.API.BlogV2.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    private User sampleUser;
    private RefreshToken sampleRefreshToken;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(42L);
        sampleUser.setName("John Doe");
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setPassword("encoded_password");
        sampleUser.setRole(Role.USER);

        sampleRefreshToken = new RefreshToken();
        sampleRefreshToken.setId(1L);
        sampleRefreshToken.setUser(sampleUser);
        sampleRefreshToken.setToken("sample-refresh-uuid-token");
        sampleRefreshToken.setExpiryDate(new Date(System.currentTimeMillis() + 604800000));
    }

    @Test
    @DisplayName("Valid login request with only email and password succeeds (HTTP 200) without requiring name")
    void testValidLoginWithoutNameSucceeds() throws Exception {
        when(userService.verifyUser(any(LoginDTO.class))).thenReturn("mock.jwt.access.token");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));
        when(refreshTokenService.createRefreshToken(42L)).thenReturn(sampleRefreshToken);

        LoginDTO loginPayload = new LoginDTO("john.doe@example.com", "SecurePassword123!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data").value("42"))
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    @DisplayName("Login request with missing email fails validation with HTTP 400")
    void testLoginMissingEmailReturnsBadRequest() throws Exception {
        String invalidPayload = """
                {
                    "password": "SecurePassword123!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login request with invalid email format fails validation with HTTP 400")
    void testLoginInvalidEmailFormatReturnsBadRequest() throws Exception {
        String invalidPayload = """
                {
                    "email": "not-a-valid-email",
                    "password": "SecurePassword123!"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login request with missing password fails validation with HTTP 400")
    void testLoginMissingPasswordReturnsBadRequest() throws Exception {
        String invalidPayload = """
                {
                    "email": "john.doe@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }
}
