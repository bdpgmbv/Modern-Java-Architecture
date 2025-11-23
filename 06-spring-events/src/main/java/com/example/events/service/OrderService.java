package com.example.events.service;

import com.example.events.event.OrderCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Service that publishes events using Spring's event mechanism.
 *
 * Modern approach: Use ApplicationEventPublisher
 */
@Service
public class OrderService {

    private final ApplicationEventPublisher eventPublisher;

    public OrderService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create order and publish event.
     * All listeners will be notified automatically!
     */
    public String createOrder(Long orderId, String customerEmail, Double amount) {
        System.out.println("\n=== Creating Order " + orderId + " ===");

        // Business logic here
        System.out.println("Order saved to database");

        // Publish event - fire and forget!
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, customerEmail, amount);
        eventPublisher.publishEvent(event);

        System.out.println("Event published - listeners will be notified");

        return "Order created: " + orderId;
    }
}
