package com.API.BlogV2.DTO;

import lombok.Data;

@Data
public class TokenRequest {
    private String refreshToken;


    public TokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}