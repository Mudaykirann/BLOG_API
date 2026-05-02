package com.API.BlogV2.Controller;

import com.API.BlogV2.DTO.TokenRequest;
import  com.API.BlogV2.Controller.TokenResponse;
import com.API.BlogV2.Entity.RefreshToken;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Service.JWTService;
import com.API.BlogV2.Service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RefreshTokenController {

    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    // ✅ Constructor Injection
    public RefreshTokenController(JWTService jwtService,
                                  RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/auth/refreshtoken")
    public ResponseEntity<?> refreshToken(
            @RequestBody TokenRequest request) {

        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token is invalid or expired"));

        User user = refreshToken.getUser();

        // ✅ FIX: include userId in JWT
        String newAccessToken = jwtService.generateToken(
                user.getEmail(),
                user.getId()
        );

        // ✅ OPTIONAL (better security): rotate refresh token
        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(user.getId());

        return ResponseEntity.ok(
                new TokenResponse(
                        newAccessToken,
                        newRefreshToken.getToken(), // rotated token
                        user.getId()
                )
        );
    }
}