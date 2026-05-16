package com.wcinv.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = UUID.randomUUID().toString();
    private final long expirationMs = 60_000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expirationMs);
    }

    @Test
    void generateToken_shouldContainUsername() {
        String token = jwtService.generateToken("testuser");

        assertNotNull(token);
        String username = jwtService.getUsernameFromToken(token);
        assertEquals("testuser", username);
    }

    @Test
    void validateToken_shouldAcceptValidToken() {
        String token = jwtService.generateToken("testuser");

        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateToken_shouldRejectExpiredToken() {
        jwtService = new JwtService(secret, 0);
        String token = jwtService.generateToken("testuser");

        assertFalse(jwtService.validateToken(token));
    }

    @Test
    void validateToken_shouldRejectTamperedToken() {
        String token = jwtService.generateToken("testuser");
        String tampered = token.substring(0, token.length() - 1) + (token.charAt(token.length() - 1) == 'A' ? 'B' : 'A');

        assertFalse(jwtService.validateToken(tampered));
    }

    @Test
    void validateToken_shouldRejectTokenSignedWithDifferentKey() {
        SecretKey otherKey = Keys.hmacShaKeyFor(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        String forgedToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(otherKey)
                .compact();

        assertFalse(jwtService.validateToken(forgedToken));
    }

    @Test
    void getUsernameFromToken_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken("admin");

        assertEquals("admin", jwtService.getUsernameFromToken(token));
    }

    @Test
    void getUsernameFromToken_shouldThrowForExpiredToken() {
        jwtService = new JwtService(secret, 10);
        String token = jwtService.generateToken("testuser");
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThrows(ExpiredJwtException.class, () -> jwtService.getUsernameFromToken(token));
    }

    @Test
    void getUsernameFromToken_shouldThrowForTamperedToken() {
        String token = jwtService.generateToken("testuser");
        String tampered = token.substring(0, token.length() - 1) + (token.charAt(token.length() - 1) == 'A' ? 'B' : 'A');

        assertThrows(JwtException.class, () -> jwtService.getUsernameFromToken(tampered));
    }

    @Test
    void validateToken_shouldRejectNullToken() {
        assertFalse(jwtService.validateToken(null));
    }

    @Test
    void validateToken_shouldRejectEmptyToken() {
        assertFalse(jwtService.validateToken(""));
    }

    @Test
    void validateToken_shouldRejectMalformedToken() {
        assertFalse(jwtService.validateToken("not.a.jwt.token"));
    }
}
