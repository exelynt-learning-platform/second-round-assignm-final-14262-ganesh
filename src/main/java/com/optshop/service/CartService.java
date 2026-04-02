package com.optshop.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.optshop.dto.CartItemResponse;
import com.optshop.dto.CartResponse;
import com.optshop.entity.Cart;
import com.optshop.entity.CartItem;
import com.optshop.entity.Product;
import com.optshop.entity.User;
import com.optshop.repository.CartItemRepository;
import com.optshop.repository.CartRepository;
import com.optshop.repository.ProductRepository;
import com.optshop.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    
    @Transactional
    public void addToCart(Long userId, Long productId, int qty) {

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepo.findByUser(user)
                .orElseGet(() -> cartRepo.save(new Cart(null, user)));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (qty <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }

        if (product.getStock() < qty) {
            throw new RuntimeException("Insufficient stock");
        }

        CartItem item = itemRepo.findByCartAndProduct(cart, product).orElse(null);

        if (item != null) {
            if (product.getStock() < item.getQuantity() + qty) {
                throw new RuntimeException("Insufficient stock");
            }
            item.setQuantity(item.getQuantity() + qty);
        } else {
            item = new CartItem(null, cart, product, qty);
        }

        itemRepo.save(item);
    }

   
    @Transactional
    public void removeItem(Long cartItemId) {
        itemRepo.deleteById(cartItemId);
    }

    
    public CartResponse viewCart(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = itemRepo.findByCart(cart);

        
        List<CartItemResponse> items = cartItems.stream()
                .map(item -> new CartItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

 
        double total = items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        return new CartResponse(cart.getId(), user.getId(), items, total);
    }
}