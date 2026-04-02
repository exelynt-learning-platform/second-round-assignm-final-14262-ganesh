package com.optshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.optshop.entity.Cart;
import com.optshop.entity.CartItem;
import com.optshop.entity.Order;
import com.optshop.entity.Product;
import com.optshop.entity.User;
import com.optshop.repository.CartItemRepository;
import com.optshop.repository.CartRepository;
import com.optshop.repository.OrderRepository;
import com.optshop.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private CartRepository cartRepo;
    @Mock
    private CartItemRepository itemRepo;
    @Mock
    private OrderRepository orderRepo;
    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        cart = new Cart(1L, user);

        product = new Product();
        product.setId(1L);
        product.setPrice(100.0);
        product.setStock(10);
    }

    @Test
    void testGetOrdersByUserId() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepo.findByUser(user)).thenReturn(List.of(new Order()));

        assertEquals(1, orderService.getOrdersByUserId(1L).size());
    }
    
    @Test
    void testGetAllOrders() {
        when(orderRepo.findAll()).thenReturn(List.of(new Order()));
        assertEquals(1, orderService.getAllOrders().size());
    }

    @Test
    void testCheckout_EmptyCart() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));
        when(itemRepo.findByCart(cart)).thenReturn(List.of());

        Exception ex = assertThrows(RuntimeException.class, () -> orderService.checkout(1L));
        assertEquals("Cart is empty", ex.getMessage());
    }

    @Test
    void testMarkOrderPaid_Success() throws StripeException {
        Order order = new Order();
        order.setUser(user);
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        CartItem item = new CartItem(1L, cart, product, 2);
        when(cartRepo.findByUser(user)).thenReturn(Optional.of(cart));
        when(itemRepo.findByCart(cart)).thenReturn(List.of(item));

        // Mock Session
        Session mockSession = mock(Session.class);
        when(mockSession.getPaymentStatus()).thenReturn("paid");

        try (MockedStatic<Session> sessionStatic = mockStatic(Session.class)) {
            sessionStatic.when(() -> Session.retrieve("session_id")).thenReturn(mockSession);

            String result = orderService.markOrderPaid(1L, "session_id");

            assertEquals("Payment Successful & Order Updated...!", result);
            assertEquals("PAID", order.getStatus());
            assertEquals(8, product.getStock()); // Deducted 2
            verify(itemRepo, times(1)).deleteAll(anyList());
        }
    }
}
