package com.example.events.listener;

import com.example.events.event.OrderCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Listener with execution order.
 *
 * @Order controls execution order for synchronous listeners
 * Lower number = higher priority
 */
@Component
public class InventoryListener {

    @EventListener
    @Order(1)  // Executes first
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("📦 [SYNC-ORDER:1] Updating inventory for order: " + event.getOrderId());
        System.out.println("   Thread: " + Thread.currentThread().getName());

        // Update inventory
        System.out.println("✓ Inventory updated");
    }
}
