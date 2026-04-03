package com.optshop.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.optshop.entity.Role;

@Data
public class AuthRequest {
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String password;

    private Role role = Role.USER;
}
