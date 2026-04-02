package com.optshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.optshop.config.AuthService;
import com.optshop.dto.AuthRequest;
import com.optshop.dto.AuthResponse;
import com.optshop.entity.Role;
import com.optshop.entity.User;
import com.optshop.repository.UserRepository;
import com.optshop.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    private AuthRequest req;
    private User user;

    @BeforeEach
    void setUp() {
        req = new AuthRequest();
        req.setEmail("test@test.com");
        req.setPassword("Password@123");

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setRole(Role.USER);
    }

    @Test
    void testRegister_Success() {
        when(repo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(encoder.encode(anyString())).thenReturn("encoded");
        when(repo.save(any(User.class))).thenReturn(user);

        String result = authService.register(req);
        assertEquals("Registered Successfully", result);
    }

    @Test
    void testRegister_WeakPassword() {
        req.setPassword("weak");
        Exception ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertTrue(ex.getMessage().contains("Password must be at least 8 characters long"));
    }

    @Test
    void testRegister_AlreadyRegistered() {
        when(repo.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void testLogin_Success() {
        when(repo.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(encoder.matches("Password@123", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken("test@test.com")).thenReturn("dummy-token");

        AuthResponse res = authService.login(req);
        assertEquals("dummy-token", res.getToken());
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(repo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        Exception ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void testLogin_UserNotFound() {
        when(repo.findByEmail(anyString())).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("User not found", ex.getMessage());
    }
}
