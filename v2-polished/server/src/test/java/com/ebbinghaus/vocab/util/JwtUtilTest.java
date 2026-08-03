package com.ebbinghaus.vocab.util;

import com.ebbinghaus.vocab.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret-key-for-jwt-unit-test-2024");
        props.setExpiration(3600000L); // 1 hour
        jwtUtil = new JwtUtil(props);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void shouldParseUserIdCorrectly() {
        String token = jwtUtil.generateToken(42L, "user", "USER");
        assertEquals(42L, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void shouldParseClaimsCorrectly() {
        String token = jwtUtil.generateToken(1L, "zhangsan", "ADMIN");
        assertEquals("zhangsan", jwtUtil.getUsernameFromToken(token));
        assertEquals("ADMIN", jwtUtil.getRoleFromToken(token));
    }

    @Test
    void shouldRejectInvalidTokens() {
        assertFalse(jwtUtil.validateToken("invalid.token"));
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    void shouldRejectTokenWithWrongSecret() {
        JwtProperties other = new JwtProperties();
        other.setSecret("a-different-secret-key-for-testing");
        JwtUtil otherUtil = new JwtUtil(other);
        String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
        assertFalse(otherUtil.validateToken(token));
    }
}
