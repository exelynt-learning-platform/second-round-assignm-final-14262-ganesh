package com.optshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.optshop.entity.Product;
import com.optshop.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    @Mock private ProductService service;
    @InjectMocks private ProductController controller;

    @Test void testAdd() {
        Product p = new Product();
        when(service.add(any())).thenReturn(p);
        ResponseEntity<Product> res = controller.add(p);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testGetAll() {
        when(service.getAll()).thenReturn(List.of(new Product()));
        ResponseEntity<List<Product>> res = controller.getAll();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(service.getById(1L)).thenReturn(new Product());
        assertEquals(HttpStatus.OK, controller.getById(1L).getStatusCode());
    }

    @Test void testUpdateProduct() {
        when(service.update(eq(1L), any())).thenReturn(new Product());
        assertEquals(HttpStatus.OK, controller.updateProduct(1L, new Product()).getStatusCode());
    }

    @Test void testDelete() {
        assertEquals(HttpStatus.NO_CONTENT, controller.delete(1L).getStatusCode());
    }
}
