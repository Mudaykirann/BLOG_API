package com.API.BlogV2.Utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration is centralized in SecurityConfig (CorsConfigurationSource)
    // to prevent duplicate headers and preflight conflicts with Spring Security.
}
