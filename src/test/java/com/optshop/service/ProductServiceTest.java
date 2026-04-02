package com.optshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.optshop.entity.Product;
import com.optshop.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductService productService;

    @Test
    void testAdd() {
        Product p = new Product();
        when(repo.save(any())).thenReturn(p);
        Product result = productService.add(p);
        assertNotNull(result);
    }

    @Test
    void testGetAll() {
        when(repo.findAll()).thenReturn(List.of(new Product()));
        List<Product> list = productService.getAll();
        assertEquals(1, list.size());
    }

    @Test
    void testGetById() {
        Product p = new Product();
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        Product result = productService.getById(1L);
        assertNotNull(result);
    }

    @Test
    void testUpdate() {
        Product existing = new Product();
        existing.setName("Old");
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenReturn(existing);

        Product p = new Product();
        p.setName("New");
        Product result = productService.update(1L, p);
        assertEquals("New", result.getName());
    }

    @Test
    void testDelete() {
        doNothing().when(repo).deleteById(1L);
        productService.delete(1L);
        verify(repo, times(1)).deleteById(1L);
    }
}
