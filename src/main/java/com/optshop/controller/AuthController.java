package com.optshop.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.optshop.service.AuthService;
import com.optshop.dto.AuthRequest;
import com.optshop.dto.AuthResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController 
{

    private final AuthService service;

    @PostMapping("/register")
    public String register(@Valid @RequestBody AuthRequest req)
    {
        return service.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest req) 
    {
        return service.login(req);
    }
}

