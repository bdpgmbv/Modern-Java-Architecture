package com.example.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Demonstrates Spring Events - modern event-driven architecture.
 *
 * Topics:
 * - Old Observer pattern (tight coupling)
 * - Modern @EventListener (loose coupling)
 * - Sync vs Async events (@Async)
 * - @TransactionalEventListener
 * - Custom application events
 */
@SpringBootApplication
@EnableAsync  // Enable async event processing
public class SpringEventsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringEventsApplication.class, args);
    }
}
