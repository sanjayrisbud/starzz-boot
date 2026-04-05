package com.sanjayrisbud.starzzboot.services;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "this-is-a-test-secret-key-for-unit-tests";
    private static final long EXPIRATION = 86400000L;

    @Test
    void generateTokenReturnsTokenWithCorrectClaims() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);
        String token = jwtService.generateToken(1, "john", "USER");
        assertEquals("1", jwtService.extractAllClaims(token).getSubject());
        assertEquals("john", jwtService.extractAllClaims(token).get("username", String.class));
        assertEquals("USER", jwtService.extractRole(token));
    }

    @Test
    void generateTokenReturnsTokenWithAdminRole() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);
        String token = jwtService.generateToken(1, "admin1", "ADMIN");
        assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    void extractRoleGivenExpiredTokenThrowsJwtException() throws InterruptedException {
        JwtService jwtService = new JwtService(SECRET, 1L);
        String token = jwtService.generateToken(1, "john", "USER");
        Thread.sleep(10);
        assertThrows(JwtException.class, () -> jwtService.extractRole(token));
    }
}
