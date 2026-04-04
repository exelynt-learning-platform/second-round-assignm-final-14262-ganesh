package com.optshop.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CartTest {

    @Test
    void testCartEntity() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        assertEquals(1L, cart.getId());
        assertEquals(user, cart.getUser());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        user.setId(2L);

        Cart cart = new Cart(1L, user);

        assertEquals(1L, cart.getId());
        assertEquals(user, cart.getUser());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1L);
        
        User user2 = new User();
        user2.setId(1L);

        Cart cart1 = new Cart(1L, user1);
        Cart cart2 = new Cart(1L, user2);
        
        Cart cart3 = new Cart(2L, user1);

        assertEquals(cart1, cart2);
        assertEquals(cart1.hashCode(), cart2.hashCode());
        assertNotEquals(cart1, cart3);
        assertNotEquals(cart1, null);
        assertNotEquals(cart1, new Object());
    }

    @Test
    void testToString() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart(1L, user);
        String cartString = cart.toString();

        assertNotNull(cartString);
        assertTrue(cartString.contains("Cart"));
        assertTrue(cartString.contains("id=1"));
        assertTrue(cartString.contains("user="));
    }
}