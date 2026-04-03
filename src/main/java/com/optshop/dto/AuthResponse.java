package com.optshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String message;

    public AuthResponse() {
        this.token = "";
        this.message = "";
    }

    public AuthResponse(String token) {
        this.token = token;
        this.message = "";
    }
}

