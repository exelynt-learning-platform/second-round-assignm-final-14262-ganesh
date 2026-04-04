package com.optshop.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optshop.dto.AuthRequest;
import com.optshop.dto.AuthResponse;
import com.optshop.entity.Role;
import com.optshop.entity.User;
import com.optshop.repository.UserRepository;
import com.optshop.security.JwtUtil;
import com.optshop.config.SecurityConstants;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    @Transactional
    public String register(AuthRequest req) {

        if (req.getPassword() == null || !req.getPassword().matches(SecurityConstants.PASSWORD_REGEX)) {
            throw new RuntimeException(SecurityConstants.PASSWORD_MESSAGE);
        }

        if (repo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("User already registered with this email");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));

        Role role = req.getRole() == null ? Role.USER : req.getRole();
        user.setRole(role);

        repo.save(user);

        return "Registered Successfully";
    }

    public AuthResponse login(AuthRequest req) {
        User user = repo.findByEmail(req.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (encoder.matches(req.getPassword(), user.getPassword())) {
            return new AuthResponse(jwtUtil.generateToken(user.getEmail()), "Login successful");
        }
        throw new RuntimeException("Invalid credentials");
    }
}
