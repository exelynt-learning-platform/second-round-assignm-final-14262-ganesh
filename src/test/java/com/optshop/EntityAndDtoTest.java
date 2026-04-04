package com.optshop;

import org.junit.jupiter.api.Test;
import java.util.List;
import com.optshop.dto.*;
import com.optshop.entity.*;
import static org.junit.jupiter.api.Assertions.*;

public class EntityAndDtoTest {

    @Test
    void testAuthRequest() {
        AuthRequest a = new AuthRequest();
        a.setEmail("e");
        a.setPassword("p");
        assertEquals("e", a.getEmail());
        assertEquals("p", a.getPassword());
        assertNotNull(a.toString());
        assertNotNull(a.hashCode());
        assertTrue(a.equals(a));
    }

    @Test
    void testAuthResponse() {
        AuthResponse a = new AuthResponse("t", "message");
        a.setToken("t2");
        assertEquals("t2", a.getToken());
    }

    @Test
    void testCartItemResponse() {
        CartItemResponse c = new CartItemResponse();
        c.setProductId(1L);
        c.setProductName("n");
        c.setPrice(10.0);
        c.setQuantity(2);
        assertEquals(1L, c.getProductId());
        assertEquals("n", c.getProductName());
        assertEquals(10.0, c.getPrice());
        assertEquals(2, c.getQuantity());
    }

    @Test
    void testCartRequest() {
        CartRequest c = new CartRequest();
        c.setUserId(1L);
        c.setProductId(1L);
        c.setQuantity(1);
        assertEquals(1L, c.getUserId());
        assertEquals(1L, c.getProductId());
        assertEquals(1, c.getQuantity());
    }

    @Test
    void testCartResponse() {
        CartResponse c = new CartResponse();
        c.setCartId(1L);
        c.setUserId(1L);
        c.setTotalPrice(100.0);
        c.setItems(List.of());
        assertEquals(1L, c.getCartId());
    }

    @Test
    void testUser() {
        User u = new User();
        u.setId(1L);
        u.setEmail("e");
        u.setPassword("p");
        u.setRole(Role.USER);
        assertEquals(1L, u.getId());
        assertEquals("e", u.getEmail());
        assertEquals("p", u.getPassword());
        assertEquals(Role.USER, u.getRole());
    }

    @Test
    void testProduct() {
        Product p = new Product();
        p.setId(1L);
        p.setName("n");
        p.setDescription("d");
        p.setPrice(10.0);
        p.setStock(10);
        p.setImageUrl("i");
        assertEquals(1L, p.getId());
        assertEquals("n", p.getName());
        assertEquals("d", p.getDescription());
        assertEquals(10.0, p.getPrice());
        assertEquals(10, p.getStock());
    }

    @Test
    void testProductLombokMethods() {
        Product p1 = new Product(1L, 1L, "n", "d", 10.0, 10, "i");
        Product p2 = new Product(1L, 1L, "n", "d", 10.0, 10, "i");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotNull(p1.toString());
        assertTrue(p1.toString().contains("Product"));

        p1.setVersion(2L);
        assertEquals(2L, p1.getVersion());
    }

    @Test
    void testOrder() {
        Order o = new Order();
        o.setId(1L);
        o.setUser(new User());
        o.setTotal(10.0);
        o.setStatus(OrderStatus.PENDING);
        o.setPaymentStatus(PaymentStatus.PENDING);
        o.setItems(List.of());
        assertEquals(1L, o.getId());
        assertEquals(10.0, o.getTotal());
        assertEquals(OrderStatus.PENDING, o.getStatus());
        assertEquals(PaymentStatus.PENDING, o.getPaymentStatus());
        assertNotNull(o.getUser());
        assertNotNull(o.getItems());
    }

    @Test
    void testOrderLombokMethods() {
        User user = new User();
        user.setId(1L);

        Order o1 = new Order(1L, user, List.of(), 10.0, OrderStatus.PENDING, PaymentStatus.PENDING);
        Order o2 = new Order(1L, user, List.of(), 10.0, OrderStatus.PENDING, PaymentStatus.PENDING);
        Order o3 = new Order(2L, user, List.of(), 20.0, OrderStatus.PAID, PaymentStatus.SUCCESS);

        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
        assertNotEquals(o1, o3);
        assertNotNull(o1.toString());
        assertTrue(o1.toString().contains("Order"));
    }

    @Test
    void testOrderItem() {
        OrderItem o = new OrderItem();
        o.setId(1L);
        o.setOrder(new Order());
        o.setProduct(new Product());
        o.setQuantity(1);
        assertEquals(1L, o.getId());
    }

    @Test
    void testOrderItemLombokMethods() {
        Order order = new Order();
        Product product = new Product();
        OrderItem o1 = new OrderItem(1L, order, product, 2);
        OrderItem o2 = new OrderItem(1L, order, product, 2);

        assertEquals(o1, o2);
        assertEquals(o1.hashCode(), o2.hashCode());
        assertNotNull(o1.toString());
        assertTrue(o1.toString().contains("OrderItem"));
        assertEquals(2, o1.getQuantity());
    }

    @Test
    void testCart() {
        Cart c = new Cart();
        c.setId(1L);
        c.setUser(new User());
        assertEquals(1L, c.getId());
    }

    @Test
    void testCartItem() {
        CartItem c = new CartItem();
        c.setId(1L);
        c.setCart(new Cart());
        c.setProduct(new Product());
        c.setQuantity(1);
        assertEquals(1L, c.getId());
        assertNotNull(c.getCart());
        assertNotNull(c.getProduct());
        assertEquals(1, c.getQuantity());
    }

    @Test
    void testCartItemLombokMethods() {
        Cart cart = new Cart();
        Product product = new Product();
        CartItem c1 = new CartItem(1L, cart, product, 2);
        CartItem c2 = new CartItem(1L, cart, product, 2);
        CartItem c3 = new CartItem(2L, cart, product, 5);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, c3);
        assertNotNull(c1.toString());
        assertTrue(c1.toString().contains("CartItem"));
    }
}
