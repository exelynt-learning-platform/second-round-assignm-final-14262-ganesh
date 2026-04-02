package com.optshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.optshop.dto.CartResponse;
import com.optshop.entity.Cart;
import com.optshop.entity.CartItem;
import com.optshop.entity.Product;
import com.optshop.entity.User;
import com.optshop.repository.CartItemRepository;
import com.optshop.repository.CartRepository;
import com.optshop.repository.ProductRepository;
import com.optshop.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepo;
    @Mock
    private CartItemRepository itemRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setPrice(100.0);
        product.setStock(10);

        cart = new Cart(1L, user);
    }

    @Test
    void testAddToCart_SuccessNewItem() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(itemRepo.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        cartService.addToCart(1L, 1L, 2);

        verify(itemRepo, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddToCart_SuccessExistingItem() {
        CartItem item = new CartItem(1L, cart, product, 2);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(itemRepo.findByCartAndProduct(cart, product)).thenReturn(Optional.of(item));

        cartService.addToCart(1L, 1L, 2);

        assertEquals(4, item.getQuantity());
        verify(itemRepo, times(1)).save(item);
    }
    
    @Test
    void testAddToCart_InsufficientStock() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        
        Exception ex = assertThrows(RuntimeException.class, () -> cartService.addToCart(1L, 1L, 20));
        assertEquals("Insufficient stock", ex.getMessage());
    }

    @Test
    void testRemoveItem() {
        doNothing().when(itemRepo).deleteById(1L);
        cartService.removeItem(1L);
        verify(itemRepo, times(1)).deleteById(1L);
    }

    @Test
    void testViewCart() {
        CartItem item = new CartItem(1L, cart, product, 2);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));
        when(itemRepo.findByCart(cart)).thenReturn(List.of(item));

        CartResponse response = cartService.viewCart(1L);

        assertEquals(1L, response.getCartId());
        assertEquals(1L, response.getUserId());
        assertEquals(200.0, response.getTotalPrice());
        assertEquals(1, response.getItems().size());
    }

}
