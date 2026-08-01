package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.LoginDTO;
import com.API.BlogV2.DTO.UserDTO;
import com.API.BlogV2.DTO.UserSignupDTO;
import com.API.BlogV2.Entity.RefreshToken;
import com.API.BlogV2.Entity.Role;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Entity.UserPrincple;
import com.API.BlogV2.Exception.UnifiedResponse;
import com.API.BlogV2.Exception.ResourceNotFoundException;
import com.API.BlogV2.Repository.UserRepository;
import com.API.BlogV2.Service.JWTService;
import com.API.BlogV2.Service.RefreshTokenService;
import com.API.BlogV2.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import com.API.BlogV2.Utils.CookieUtil;


import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "api/v1")
@Slf4j
public class UserController {

    private  final UserService userService;


    @Autowired
    private JWTService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CookieUtil cookieUtil;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponse<List<UserDTO>>> getAllUsers() throws IllegalAccessException {
        List<UserDTO> users = userService.getAllUser();
        return ResponseEntity.ok(UnifiedResponse.ok( "Users Fetched", users));
    }

    @GetMapping(path = "/users/{id}")
    public ResponseEntity<UnifiedResponse<UserDTO>> getUserDetails(@PathVariable("id") Long id) {
        UserDTO user = userService.getUserDetails(id);
        return ResponseEntity.ok(UnifiedResponse.ok( "User fetched successfully", user));
    }

    @PostMapping(path = "/auth/login")
    public ResponseEntity<UnifiedResponse<String>> login(@RequestBody LoginDTO u) {
        String token = userService.verifyUser(u);

        User authenticatedUser = userRepository.findByEmail(u.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", u.getEmail()));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getId());

        // Create Secure HttpOnly Cookies for XSS protection
        ResponseCookie jwtCookie = cookieUtil.createHttpOnlyCookie("accessToken", token, 15 * 60); // 15 mins
        ResponseCookie refreshCookie = cookieUtil.createHttpOnlyCookie("refreshToken", refreshToken.getToken(), 7 * 24 * 60 * 60); // 7 days

        log.info("User {} successfully logged in", u.getEmail());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(UnifiedResponse.ok("Login successful", authenticatedUser.getId().toString()));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UnifiedResponse<Void>> registerUser(
            @Valid @RequestBody UserSignupDTO dto) {

        userService.registerUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UnifiedResponse.ok("User Registered successfully", null));
    }

    @DeleteMapping(path = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public ResponseEntity<UnifiedResponse<Void>> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(UnifiedResponse.ok( "User Deleted successfully", null));
    }

    @PutMapping(path = "/users/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public ResponseEntity<UnifiedResponse<Void>> updateUser(@PathVariable("id") Long id, @RequestBody UserDTO userDTO) throws AccessDeniedException {
        userService.updateUser(id, userDTO);
        return ResponseEntity.ok(UnifiedResponse.ok( "User Updated Successfully", null));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<UnifiedResponse<String>> logout() {
        // 1. Get the current user from SecurityContext safely
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserPrincple) {
            UserPrincple userDetails = (UserPrincple) auth.getPrincipal();
            userRepository.findById(userDetails.getId()).ifPresent(user -> {
                // 2. Delete the refresh token from DB if user exists
                refreshTokenService.deleteByUser(user);
            });
        }

        // 3. Clear the cookies by setting maxAge to 0 (always do this to ensure client state is cleared)
        ResponseCookie jwtCookie = cookieUtil.createHttpOnlyCookie("accessToken", "", 0);
        ResponseCookie refreshCookie = cookieUtil.createHttpOnlyCookie("refreshToken", "", 0);

        log.info("User successfully logged out");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(UnifiedResponse.ok("Logged out successfully. Tokens invalidated.",null));
    }


    // In your existing UserController.java — ADD these endpoints
    @PatchMapping("/users/{id}/profile-pic")
    public ResponseEntity<UnifiedResponse<UserDTO>> updateProfilePic(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String imageUrl = body.get("imageUrl"); // URL returned by ImageKit after upload
        UserDTO updated = userService.updateProfilePic(id, imageUrl);
        return ResponseEntity.ok(UnifiedResponse.ok("Profile pic updated successfully", updated));
    }

    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponse<Void>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String roleStr = body.get("role");
        Role role = Role.valueOf(roleStr.toUpperCase());
        userService.updateUserRole(id, role);
        return ResponseEntity.ok(UnifiedResponse.ok("User role updated to " + role, null));
    }

    @GetMapping("/users/{id}/profile-pic/thumbnail")
    public ResponseEntity<Map<String, String>> getProfilePicThumbnail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "150") int width,
            @RequestParam(defaultValue = "150") int height) {

        String url = userService.getResizedProfilePic(id, width, height);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
