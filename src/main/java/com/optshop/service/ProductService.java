package com.optshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.optshop.entity.Product;
import com.optshop.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService 
{

    private final ProductRepository repo;

    public Product add(Product p) 
    {
    	return repo.save(p); 
    }

    public List<Product> getAll() 
    { 
    	return repo.findAll(); 
    }

    public Product update(Long id, Product p) 
    {
        Product existing = repo.findById(id).orElseThrow();
        existing.setName(p.getName());
        existing.setPrice(p.getPrice());
        return repo.save(existing);
    }

    public void delete(Long id) 
    { 
    	repo.deleteById(id);
    }
    
}