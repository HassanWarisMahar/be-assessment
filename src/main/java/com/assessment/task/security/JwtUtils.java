package com.assessment.task.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    private static final String SECRET_KEY = "Jb67s+KL9wPhtVbZX9Aq3mJ2QojFdRz7Xyeas47+jqo="; // Replace with a secure key

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1-hour expiration
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();

        log.info("Generated JWT for user: {}", username);
        log.debug("JWT Token: {}", token);
        return token;
    }

    public String extractUsername(String token) {
        try {
            String username = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            log.debug("Extracted username from JWT: {}", username);
            return username;
        } catch (JwtException e) {
            log.error("Failed to extract username from JWT", e);
            throw e;
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            boolean isValid = extractedUsername.equals(username) && !isTokenExpired(token);

            if (isValid) {
                log.info("JWT token is valid for user: {}", username);
            } else {
                log.warn("Invalid JWT token for user: {}", username);
            }
            return isValid;
        } catch (JwtException e) {
            log.error("JWT validation failed", e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            boolean isExpired = expiration.before(new Date());
            log.debug("JWT expiration time: {}, isExpired: {}", expiration, isExpired);
            return isExpired;
        } catch (JwtException e) {
            log.error("Failed to check JWT expiration", e);
            throw e;
        }
    }
}
