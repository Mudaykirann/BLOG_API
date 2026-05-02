package com.API.BlogV2.Service;

import com.API.BlogV2.Entity.RefreshToken;
import com.API.BlogV2.Entity.User;
import com.API.BlogV2.Repository.RefreshTokenRepository;
import com.API.BlogV2.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // 7 days expiry
    private final long refreshTokenDurationMs = 7 * 24 * 60 * 60 * 1000;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // ✅ CREATE NEW REFRESH TOKEN
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Optional: delete old token (one token per user)
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush(); // Forces the DELETE to happen NOW as it won't deleting the refresh token even when user logs out

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                new Date(System.currentTimeMillis() + refreshTokenDurationMs)
        );

        return refreshTokenRepository.saveAndFlush(refreshToken);
    }

    // ✅ FIND TOKEN
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // ✅ VERIFY EXPIRATION
    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }

        return token;
    }

    // ✅ DELETE BY USER (logout)
    public void deleteByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.deleteByUser(user);
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}