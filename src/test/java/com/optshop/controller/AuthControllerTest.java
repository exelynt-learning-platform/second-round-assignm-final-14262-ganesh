package com.optshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.optshop.config.AuthService;
import com.optshop.dto.AuthRequest;
import com.optshop.dto.AuthResponse;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock private AuthService service;
    @InjectMocks private AuthController controller;

    @Test
    void testRegister() {
        when(service.register(any())).thenReturn("Reg");
        assertEquals("Reg", controller.register(new AuthRequest()));
    }

    @Test
    void testLogin() {
        AuthResponse res = new AuthResponse("dummy");
        when(service.login(any())).thenReturn(res);
        assertEquals(res, controller.login(new AuthRequest()));
    }
}
