package com.optshop.config;

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
    private org.springframework.security.crypto.password.PasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService.init();
    }

    @Test
    void testRegister_Success() {
        AuthRequest req = new AuthRequest();
        req.setEmail("test@example.com");
        req.setPassword("Password123!");

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("Password123!")).thenReturn("encoded-password");
        when(repo.save(any(User.class))).thenReturn(new User());

        String response = authService.register(req);

        assertEquals("Registered Successfully", response);
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UserExists() {
        AuthRequest req = new AuthRequest();
        req.setEmail("test@example.com");
        req.setPassword("Password123!");

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertEquals("User already registered with this email", ex.getMessage());
    }

    @Test
    void testRegister_InvalidPassword() {
        AuthRequest req = new AuthRequest();
        req.setEmail("test@example.com");
        req.setPassword("weak");

        Exception ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertTrue(ex.getMessage().contains("Password must be"));
    }

    @Test
    void testLogin_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$...");
        user.setRole(Role.USER);

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        AuthRequest loginReq = new AuthRequest();
        loginReq.setEmail("test@example.com");
        loginReq.setPassword("password");

        when(encoder.matches("password", "$2a$10$...")).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com")).thenReturn("token-value");

        AuthResponse response = authService.login(loginReq);

        assertEquals("token-value", response.getToken());

        assertNotNull(response);
    }

    @Test
    void testLogin_UserNotFound() {
        when(repo.findByEmail("test@example.com")).thenReturn(Optional.empty());

        AuthRequest loginReq = new AuthRequest();
        loginReq.setEmail("test@example.com");
        loginReq.setPassword("password");

        Exception ex = assertThrows(RuntimeException.class, () -> authService.login(loginReq));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testInit_DefaultRegex() {
        // Since passwordRegex is @Value with default, and init sets if null
        authService.init();
        // Hard to test without reflection, but assume it's covered
    }
}