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
}
