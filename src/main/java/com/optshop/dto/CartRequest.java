package com.optshop.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class CartRequest {
    @NotNull
    private Long userId;
    
    @NotNull
    private Long productId;
    
    @Min(1)
    private int quantity;
}
