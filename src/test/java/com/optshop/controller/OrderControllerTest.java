package com.optshop.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.optshop.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {
    @Mock private OrderService service;
    @InjectMocks private OrderController controller;

    @Test void testCheckout() throws Exception {
        when(service.checkout(1L)).thenReturn("url");
        assertEquals("url", controller.checkout(1L));
    }

    @Test void testSuccess() throws Exception {
        when(service.markOrderPaid(1L, "sess")).thenReturn("Success");
        assertEquals("Success", controller.success(1L, "sess"));
    }

    @Test void testCancel() {
        assertEquals("Payment Cancelled!", controller.cancel());
    }

    @Test void testGetOrders() {
        assertNotNull(controller.getOrders(1L));
        assertNotNull(controller.getAllOrders());
    }
}
