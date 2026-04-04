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
import com.optshop.repository.ProductRepository;
import com.optshop.repository.UserRepository;
import com.optshop.exception.InsufficientStockException;
import jakarta.persistence.OptimisticLockException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    private static final int DEFAULT_MULTIPLIER = 100;

    @Value("${app.currency.multiplier:" + DEFAULT_MULTIPLIER + "}")
    private int currencyMultiplier;

    @Value("${app.currency:usd}")
    private String currency;

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

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public String checkout(Long userId) throws StripeException {

        User user = getUser(userId);
        Cart cart = getCart(user);
        List<CartItem> cartItems = getCartItems(cart);

        validateCart(cartItems);

        double total = calculateTotal(cartItems);

        Order order = createOrder(user, total, cartItems);

        return createStripeCheckoutSession(order, user, cartItems);
    }
    
    private User getUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private Cart getCart(User user) {
        return cartRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }
    
    private List<CartItem> getCartItems(Cart cart) {
        List<CartItem> items = itemRepo.findByCart(cart);
        if (items.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        return items;
    }
    
    private void validateCart(List<CartItem> cartItems) {
        for (CartItem ci : cartItems) {
            confirmStock(ci.getProduct(), ci.getQuantity());
        }
    }
    
    private double calculateTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
    }
    
    private Order createOrder(User user, double total, List<CartItem> cartItems) {

        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        orderRepo.save(order);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem c : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(c.getProduct());
            oi.setQuantity(c.getQuantity());
            orderItems.add(oi);
        }

        order.setItems(orderItems);
        return orderRepo.save(order);
    }
    
    private String createStripeCheckoutSession(Order order, User user, List<CartItem> cartItems) throws StripeException {

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        
        for (CartItem item : cartItems) {

            long amountInCents = Math.round(item.getProduct().getPrice() * currencyMultiplier);
            
            SessionCreateParams.LineItem.PriceData.ProductData productData = 
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(item.getProduct().getName())
                    .build();
            
            SessionCreateParams.LineItem.PriceData priceData = 
                SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(currency)
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
        

        Session session = Session.retrieve(sessionId);
        

        if ("paid".equals(session.getPaymentStatus())) {
            order.setStatus(OrderStatus.PAID);
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            orderRepo.save(order);
            

            User user = order.getUser();
            Cart cart = cartRepo.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            List<CartItem> cartItems = itemRepo.findByCart(cart);
            
            for (CartItem ci : cartItems) {
                Product p = ci.getProduct();
                if (p.getStock() >= ci.getQuantity()) {
                    p.setStock(p.getStock() - ci.getQuantity());
                    try {
                        productRepo.save(p);
                    } catch (OptimisticLockException e) {
                        throw new RuntimeException("Stock update conflict. Please retry.");
                    }
                } else {
                    throw new InsufficientStockException("Insufficient stock when finalizing payment for product: " + p.getName());
                }
            }
            
            itemRepo.deleteAll(cartItems);
            
            return "Payment Successful & Order Updated...!";
        } else {
            return "Payment not completed";
        }
    }
}
