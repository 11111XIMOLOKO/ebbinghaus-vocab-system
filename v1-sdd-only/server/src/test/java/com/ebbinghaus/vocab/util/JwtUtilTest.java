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
        String token = jwtUtil.generateToken(42L, "testuser", "USER");

        assertEquals(42L, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void shouldParseUsernameAndRole() {
        String token = jwtUtil.generateToken(1L, "zhangsan", "ADMIN");

        assertEquals("zhangsan", jwtUtil.getUsernameFromToken(token));
        assertEquals("ADMIN", jwtUtil.getRoleFromToken(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(jwtUtil.validateToken("invalid.token.string"));
        assertFalse(jwtUtil.validateToken(""));
        assertNull(jwtUtil.getUserIdFromToken("bad-token"));
    }

    @Test
    void shouldRejectTokenWithWrongSecret() {
        JwtProperties otherProps = new JwtProperties();
        otherProps.setSecret("a-different-secret-key-for-testing");
        otherProps.setExpiration(3600000L);
        JwtUtil otherJwtUtil = new JwtUtil(otherProps);

        String token = jwtUtil.generateToken(1L, "admin", "ADMIN");

        // 用不同 secret 的 JwtUtil 校验应失败
        assertFalse(otherJwtUtil.validateToken(token));
    }

    @Test
    void shouldReturnNullForNullToken() {
        assertNull(jwtUtil.getUserIdFromToken(null));
    }
}
