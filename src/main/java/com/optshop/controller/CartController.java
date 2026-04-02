package com.optshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.optshop.dto.CartRequest;
import com.optshop.dto.CartResponse;
import com.optshop.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

  
    @PostMapping
    public ResponseEntity<String> add(@Valid @RequestBody CartRequest req) {
        service.addToCart(req.getUserId(), req.getProductId(), req.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body("Added to The Cart");
    }

   
    @GetMapping("/{userId}")
    public CartResponse view(@PathVariable Long userId) {
        return service.viewCart(userId); // returns CartResponse now
    }

   
    @DeleteMapping("/{cartItemId}")
    public String remove(@PathVariable Long cartItemId) {
        service.removeItem(cartItemId);
        return "Removed";
    }
}