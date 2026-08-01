package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.TokenRequest;
import  com.API.BlogV2.Controller.TokenResponse;
import com.API.BlogV2.Entity.RefreshToken;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Service.JWTService;
import com.API.BlogV2.Service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import com.API.BlogV2.Exception.UnifiedResponse;
import com.API.BlogV2.Utils.CookieUtil;

@RestController
@RequestMapping("/api/v1")
public class RefreshTokenController {

    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtil cookieUtil;

    // ✅ Constructor Injection
    public RefreshTokenController(JWTService jwtService,
                                  RefreshTokenService refreshTokenService,
                                  CookieUtil cookieUtil) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/auth/refreshtoken")
    public ResponseEntity<UnifiedResponse<String>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String requestRefreshToken) {

        if (requestRefreshToken == null || requestRefreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is missing");
        }

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token is invalid or expired"));

        User user = refreshToken.getUser();

        // Rotate token - delete old one
        refreshTokenService.deleteByUser(user);

        // Generate new tokens (ensure we pass Name, not Email, to match MyUserDetailsService)
        String newAccessToken = jwtService.generateToken(
                user.getName(),
                user.getId()
        );

        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(user.getId());

        // Create Secure HttpOnly Cookies for XSS protection
        ResponseCookie jwtCookie = cookieUtil.createHttpOnlyCookie("accessToken", newAccessToken, 15 * 60); // 15 mins
        ResponseCookie refreshCookie = cookieUtil.createHttpOnlyCookie("refreshToken", newRefreshToken.getToken(), 7 * 24 * 60 * 60); // 7 days

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(UnifiedResponse.ok("Tokens refreshed successfully", null));
    }
}