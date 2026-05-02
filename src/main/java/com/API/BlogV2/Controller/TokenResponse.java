package com.API.BlogV2.Controller;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
    // A simple DTO for the response
public record TokenResponse(String accessToken, String refreshToken, Long id) {
}
