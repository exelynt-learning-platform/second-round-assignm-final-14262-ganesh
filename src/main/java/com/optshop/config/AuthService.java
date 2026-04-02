package com.optshop.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

import com.optshop.dto.AuthRequest;
import com.optshop.dto.AuthResponse;
import com.optshop.entity.Role;
import com.optshop.entity.User;
import com.optshop.repository.UserRepository;
import com.optshop.security.JwtUtil;

import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    @Value("${app.security.password.regex:}")
    private String passwordRegex;

    @PostConstruct
    public void init() {
        if (passwordRegex == null || passwordRegex.isEmpty()) {
            passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
        }
    }

    @Transactional
    public String register(AuthRequest req) {
        // Strict password validation
        if (req.getPassword() == null || !req.getPassword().matches(passwordRegex)) {
            throw new RuntimeException("Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character.");
        }

        if (repo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("User already registered with this email");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));

      
        user.setRole(Role.USER);

        repo.save(user);

        return "Registered Successfully";
    }

    public AuthResponse login(AuthRequest req) {
        User user = repo.findByEmail(req.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (encoder.matches(req.getPassword(), user.getPassword())) {
            return new AuthResponse(jwtUtil.generateToken(user.getEmail()));
        }
        throw new RuntimeException("Invalid credentials");
    }
}