package com.optshop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optshop.entity.Product;
import com.optshop.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService 
{

    private final ProductRepository repo;

    @Transactional
    public Product add(Product p) 
    {
    	return repo.save(p); 
    }

    public Product getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getAll() 
    { 
    	return repo.findAll(); 
    }

    @Transactional
    public Product update(Long id, Product p) 
    {
        Product existing = repo.findById(id).orElseThrow();
        existing.setName(p.getName());
        existing.setDescription(p.getDescription());
        existing.setPrice(p.getPrice());
        existing.setStock(p.getStock());
        existing.setImageUrl(p.getImageUrl());
        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) 
    { 
    	repo.deleteById(id);
    }
    
}