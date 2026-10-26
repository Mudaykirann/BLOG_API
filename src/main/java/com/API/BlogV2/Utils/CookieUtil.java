package com.API.BlogV2.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${app.cookie.secure:false}")
    private boolean isSecure;

    /**
     * Creates a secure HttpOnly cookie for storing tokens.
     *
     * Security Explanations:
     * - HttpOnly: Prevents JavaScript (and thus XSS attackers) from accessing the cookie.
     * - Secure: Ensures the cookie is only sent over HTTPS in production (controlled via app.cookie.secure).
     * - SameSite=Lax: Protects against most CSRF attacks while allowing normal navigation (per user request).
     */
    public ResponseCookie createHttpOnlyCookie(String name, String value, long maxAgeSeconds) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")
                .build();
    }
}
