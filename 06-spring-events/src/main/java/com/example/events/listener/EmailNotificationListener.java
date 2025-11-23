package com.example.events.listener;

import com.example.events.event.OrderCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Synchronous event listener - runs in same thread.
 *
 * @EventListener - Modern way (Spring 4.2+)
 * No need to implement ApplicationListener interface
 */
@Component
public class EmailNotificationListener {

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("📧 [SYNC] Sending email to: " + event.getCustomerEmail());
        System.out.println("   Thread: " + Thread.currentThread().getName());

        // Simulate email sending
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("✓ Email sent for order: " + event.getOrderId());
    }
}
