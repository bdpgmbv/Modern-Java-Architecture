package com.example.events.listener;

import com.example.events.event.OrderCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Asynchronous event listener - runs in separate thread.
 *
 * @Async makes this listener non-blocking
 * Main thread doesn't wait for this to complete
 */
@Component
public class AsyncAnalyticsListener {

    @Async  // Runs in separate thread!
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("📊 [ASYNC] Processing analytics for order: " + event.getOrderId());
        System.out.println("   Thread: " + Thread.currentThread().getName());

        // Simulate slow analytics processing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("✓ Analytics processed for order: " + event.getOrderId());
    }
}
