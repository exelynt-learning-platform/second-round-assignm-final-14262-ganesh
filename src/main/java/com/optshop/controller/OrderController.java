package com.optshop.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.optshop.entity.Order;
import com.optshop.service.OrderService;
import com.stripe.exception.StripeException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping("/checkout/{userId}")
    public String checkout(@PathVariable Long userId) throws StripeException {
        String checkoutUrl = service.checkout(userId);
        return checkoutUrl; 
    }

    @GetMapping("/success/{orderId}")
    public String success(@PathVariable Long orderId, @RequestParam String session_id) throws StripeException {
        return service.markOrderPaid(orderId, session_id);
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "Payment Cancelled!";
    }

    @GetMapping("/{userId}")
    public List<Order> getOrders(@PathVariable Long userId) {
        return service.getOrdersByUserId(userId);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return service.getAllOrders();
    }
}
