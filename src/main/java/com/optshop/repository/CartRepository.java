package com.optshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.optshop.entity.Cart;
import com.optshop.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
