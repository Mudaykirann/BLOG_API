package com.API.BlogV2;

import com.API.BlogV2.DTO.UserSignupDTO;
import com.API.BlogV2.Entity.Role;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Repository.UserRepository;
import com.API.BlogV2.Service.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdminSecretRegistrationSecurityTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private org.springframework.core.env.Environment env;

    @MockitoBean
    private UserRepository userRepository;

    private String originalAdminSecret;
    private org.springframework.core.env.Environment originalEnv;

    @org.junit.jupiter.api.BeforeEach
    void saveOriginalState() {
        originalAdminSecret = (String) ReflectionTestUtils.getField(userService, "adminSecret");
        originalEnv = (org.springframework.core.env.Environment) ReflectionTestUtils.getField(userService, "environment");
    }

    @org.junit.jupiter.api.AfterEach
    void restoreOriginalState() {
        ReflectionTestUtils.setField(userService, "adminSecret", originalAdminSecret);
        ReflectionTestUtils.setField(userService, "environment", originalEnv);
    }

    @Test
    @DisplayName("Insecure 'default_admin_secret' must NOT elevate user to ADMIN")
    void testDefaultAdminSecretDoesNotElevate() {
        when(userRepository.existsByEmail("attacker@test.com")).thenReturn(false);

        UserSignupDTO dto = new UserSignupDTO();
        dto.setName("Attacker");
        dto.setEmail("attacker@test.com");
        dto.setPassword("Secret123!");
        dto.setAdminSecret("default_admin_secret");

        userService.registerUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(Role.USER, savedUser.getRole(), "User registered with 'default_admin_secret' should remain Role.USER");
    }

    @Test
    @DisplayName("Valid environment secret elevates user to ADMIN")
    void testValidAdminSecretElevatesUser() {
        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);

        UserSignupDTO dto = new UserSignupDTO();
        dto.setName("Admin");
        dto.setEmail("admin@test.com");
        dto.setPassword("Secret123!");
        dto.setAdminSecret("dev_local_admin_secret_2026");

        userService.registerUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(Role.ADMIN, savedUser.getRole(), "User registered with configured secret should be Role.ADMIN");
    }

    @Test
    @DisplayName("Fails startup validation if prod profile is active and admin secret is missing/default")
    void testProdStartupValidation() {
        MockEnvironment prodEnv = new MockEnvironment();
        prodEnv.setActiveProfiles("prod");

        // Test with "default_admin_secret"
        ReflectionTestUtils.setField(userService, "environment", prodEnv);
        ReflectionTestUtils.setField(userService, "adminSecret", "default_admin_secret");
        assertThrows(IllegalStateException.class, () -> userService.validateAdminSecret());

        // Test with blank
        ReflectionTestUtils.setField(userService, "adminSecret", "   ");
        assertThrows(IllegalStateException.class, () -> userService.validateAdminSecret());

        // Test with null
        ReflectionTestUtils.setField(userService, "adminSecret", null);
        assertThrows(IllegalStateException.class, () -> userService.validateAdminSecret());

        // Test with valid secret in prod -> succeeds
        ReflectionTestUtils.setField(userService, "adminSecret", "strong_production_secret_98765");
        assertDoesNotThrow(() -> userService.validateAdminSecret());
    }
}
