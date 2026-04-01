package com.optshop.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.optshop.config.AuthService;
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
    public String register(@RequestBody AuthRequest req)
    {
        return service.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) 
    {
        return service.login(req);
    }
}

