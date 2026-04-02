package com.optshop.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.optshop.service.CartService;
import com.optshop.dto.CartRequest;
import com.optshop.dto.CartResponse;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {
    @Mock private CartService service;
    @InjectMocks private CartController controller;

    @Test void testAdd() {
        CartRequest req = new CartRequest();
        req.setUserId(1L); req.setProductId(1L); req.setQuantity(2);
        ResponseEntity<String> res = controller.add(req);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testView() {
        when(service.viewCart(1L)).thenReturn(new CartResponse());
        assertNotNull(controller.view(1L));
    }

    @Test void testRemove() {
        ResponseEntity<Void> response = controller.remove(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
