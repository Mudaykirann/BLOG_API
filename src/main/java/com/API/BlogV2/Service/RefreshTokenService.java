package com.API.BlogV2.Service;

import com.API.BlogV2.Entity.RefreshToken;
import com.API.BlogV2.Repository.RefreshTokenRepository;
import com.API.BlogV2.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@Service
public class RefreshTokenService {


    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken createRefreshToken(Long userId) {
        return userRepository.findById(userId).map(user -> {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Date.from(Instant.now().plusMillis(604800000)));
            return refreshTokenRepository.save(refreshToken);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Date.from(Instant.now())) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please signin.");
        }
        return token;
    }

}
