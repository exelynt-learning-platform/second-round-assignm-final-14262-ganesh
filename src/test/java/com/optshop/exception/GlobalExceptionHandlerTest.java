package com.optshop.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Test error");
        ResponseEntity<String> response = handler.handle(ex);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Test error", response.getBody());
    }

    @Test
    void testHandleInsufficientStockException() {
        InsufficientStockException ex = new InsufficientStockException("Not enough stock");
        ResponseEntity<String> response = handler.handleStock(ex);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Not enough stock", response.getBody());
    }

    @Test
    void testHandleProductNotFoundException() {
        ProductNotFoundException ex = new ProductNotFoundException("Product 1 not found");
        ResponseEntity<String> response = handler.handleProduct(ex);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("Product 1 not found", response.getBody());
    }
}
