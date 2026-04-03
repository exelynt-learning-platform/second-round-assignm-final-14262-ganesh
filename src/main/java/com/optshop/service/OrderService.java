package com.optshop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.optshop.entity.Cart;
import com.optshop.entity.CartItem;
import com.optshop.entity.Order;
import com.optshop.entity.OrderItem;
import com.optshop.entity.OrderStatus;
import com.optshop.entity.PaymentStatus;
import com.optshop.entity.Product;
import com.optshop.entity.User;
import com.optshop.repository.CartItemRepository;
import com.optshop.repository.CartRepository;
import com.optshop.repository.OrderRepository;
import com.optshop.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final int CENTS_IN_DOLLAR = 100;

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;

    @Value("${app.url}")
    private String baseUrl;

   
    public List<Order> getOrdersByUserId(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepo.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    private void confirmStock(Product product, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public String checkout(Long userId) throws StripeException {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> cartItems = itemRepo.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = cartItems.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        for (CartItem ci : cartItems) {
            confirmStock(ci.getProduct(), ci.getQuantity());
        }

        List<OrderItem> orderItems = new ArrayList<>();

        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        orderRepo.save(order);

        for (CartItem c : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(c.getProduct());
            oi.setQuantity(c.getQuantity());
            orderItems.add(oi);
        }

        order.setItems(orderItems);
        orderRepo.save(order);

        // Create Stripe Checkout Session
        String checkoutUrl = createStripeCheckoutSession(order, user, cartItems);
        
        return checkoutUrl;
    }
    
    private String createStripeCheckoutSession(Order order, User user, List<CartItem> cartItems) throws StripeException {
        // Build line items for Stripe
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            // Convert price to cents/pence (Stripe uses smallest currency unit)
            long amountInCents = (long) (item.getProduct().getPrice() * CENTS_IN_DOLLAR);
            
            SessionCreateParams.LineItem.PriceData.ProductData productData = 
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(item.getProduct().getName())
                    .build();
            
            SessionCreateParams.LineItem.PriceData priceData = 
                SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("usd")
                    .setUnitAmount(amountInCents)
                    .setProductData(productData)
                    .build();
            
            SessionCreateParams.LineItem lineItem = 
                SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getQuantity())
                    .setPriceData(priceData)
                    .build();
            
            lineItems.add(lineItem);
        }
        
        // Create checkout session with metadata to track order
        SessionCreateParams params = SessionCreateParams.builder()
            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(baseUrl + "/orders/success/" + order.getId() + "?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(baseUrl + "/orders/cancel")
            .addAllLineItem(lineItems)
            .putMetadata("order_id", order.getId().toString())
            .putMetadata("user_id", user.getId().toString())
            .build();
        
        Session session = Session.create(params);
        
        return session.getUrl(); // Returns the Stripe Checkout URL
    }

   
    @Transactional(rollbackFor = Exception.class)
    public String markOrderPaid(Long orderId, String sessionId) throws StripeException {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Verify the payment session
        Session session = Session.retrieve(sessionId);
        
        // Check if payment was successful
        if ("paid".equals(session.getPaymentStatus())) {
            order.setStatus(OrderStatus.PAID);
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            orderRepo.save(order);
            
            // Clear the cart after successful payment
            User user = order.getUser();
            Cart cart = cartRepo.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            List<CartItem> cartItems = itemRepo.findByCart(cart);
            
            for (CartItem ci : cartItems) {
                Product p = ci.getProduct();
                if (p.getStock() >= ci.getQuantity()) {
                    p.setStock(p.getStock() - ci.getQuantity());
                } else {
                    throw new RuntimeException("Insufficient stock when finalizing payment for product: " + p.getName());
                }
            }
            
            itemRepo.deleteAll(cartItems);
            
            return "Payment Successful & Order Updated...!";
        } else {
            return "Payment not completed";
        }
    }
}