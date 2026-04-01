package com.optshop.dto;

import lombok.Data;

@Data
public class CartRequest {
    private Long userId;
    private Long productId;
    private int quantity;
}
