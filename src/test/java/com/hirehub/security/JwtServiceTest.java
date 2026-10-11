package com.hirehub.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hirehub.entity.User;

import io.jsonwebtoken.Jwts;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        byte[] secretBytes = new byte[32];

        // Test-only key; never use this in production.
        for (int i = 0; i < secretBytes.length; i++) {
            secretBytes[i] = (byte) (i + 1);
        }

        String testSecret = Base64.getEncoder()
                .encodeToString(secretBytes);

        jwtService = new JwtService(testSecret);
    }
    
    @Test
    void generateTokenShouldContainCorrectUserId() {
        // Arrange
        User user = new User();
        user.setId(101L);
        user.setEmail("candidate@example.com");

        // Act
        String token = jwtService.generateToken(user);
        Long extractedUserId = jwtService.extractUserId(token);

        // Assert
        assertEquals(101L, extractedUserId);
    }
    
    @Test
    void expiredTokenShouldBeRejected() {
        // Arrange: Create a token that expired one minute ago
        String token = Jwts.builder()
                .subject("101")
                .issuedAt(new java.util.Date(
                        System.currentTimeMillis() - 120_000))
                .expiration(new java.util.Date(
                        System.currentTimeMillis() - 60_000))
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                                Base64.getDecoder().decode(
                                        Base64.getEncoder().encodeToString(
                                                new byte[] {
                                                    1, 2, 3, 4, 5, 6, 7, 8,
                                                    9, 10, 11, 12, 13, 14, 15, 16,
                                                    17, 18, 19, 20, 21, 22, 23, 24,
                                                    25, 26, 27, 28, 29, 30, 31, 32
                                                }))),
                        io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        // Act and Assert: An expired token must be rejected
        assertThrows(
                io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.extractUserId(token));
    }
}