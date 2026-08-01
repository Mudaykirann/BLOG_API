package com.API.BlogV2.Utils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // In-memory cache for IP addresses to buckets.
    // For a multi-instance distributed setup, this should be backed by RedisProxyManager.
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Limit: 10 requests per 5 minutes (as requested by user)
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(5)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Only apply rate limiting to authentication endpoints to prevent brute force
        if (requestURI.startsWith("/api/v1/auth/login") || requestURI.startsWith("/api/v1/auth/register")) {

            String ip = getClientIP(request);
            Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());

            if (bucket.tryConsume(1)) {
                // Allowed
                filterChain.doFilter(request, response);
            } else {
                // Rate limit exceeded (Brute Force Protection)
                response.setStatus(429); // HTTP 429 Too Many Requests
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too many login attempts. Please wait 5 minutes and try again.\"}");
                return;
            }
        } else {
            // Bypass filter for other endpoints
            filterChain.doFilter(request, response);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
