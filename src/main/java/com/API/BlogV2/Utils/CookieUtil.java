package com.API.BlogV2.Utils;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    /**
     * Creates a secure HttpOnly cookie for storing tokens.
     *
     * Security Explanations:
     * - HttpOnly: Prevents JavaScript (and thus XSS attackers) from accessing the cookie.
     * - Secure: Ensures the cookie is only sent over HTTPS in production.
     * - SameSite=Lax: Protects against most CSRF attacks while allowing normal navigation (per user request).
     */
    public ResponseCookie createHttpOnlyCookie(String name, String value, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // Note: Set to 'true' in a real production environment with HTTPS
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Lax") // As per user request: currently no domain, use default Lax
                .build();
    }
}
