package com.optshop.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "SECRET", "fjlfjkshfkshuirnngklnkhdlksbhksbgjkdsdarwed123456");
    }

    @Test
    void testGenerateAndExtract() {
        String token = jwtUtil.generateToken("test@test.com");
        assertNotNull(token);
        
        String email = jwtUtil.extractEmail(token);
        assertEquals("test@test.com", email);
        
        assertTrue(jwtUtil.validateToken(token));
    }
    
    @Test
    void testValidateToken_Invalid() {
        assertFalse(jwtUtil.validateToken("invalid-token"));
    }
}
