package com.optshop.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.optshop.dto.AuthRequest;
import com.optshop.dto.AuthResponse;
import com.optshop.entity.Role;
import com.optshop.entity.User;
import com.optshop.repository.UserRepository;
import com.optshop.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public String register(AuthRequest req) {

       
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
        User user = repo.findByEmail(req.getEmail()).orElseThrow();
        if (encoder.matches(req.getPassword(), user.getPassword())) {
            return new AuthResponse(jwtUtil.generateToken(user.getEmail()));
        }
        throw new RuntimeException("Invalid credentials");
    }
}