# Project 6: Spring Events

## Overview
Demonstrates Spring's event-driven architecture - decouple components using application events.

## Old vs New Approach

### Old Observer Pattern (Tight Coupling)
```java
// Publisher knows about all observers
public class OrderService {
    private EmailService emailService;
    private SmsService smsService;
    private AnalyticsService analyticsService;

    public void createOrder() {
        // Business logic
        emailService.send();
        smsService.send();
        analyticsService.track();
        // Must modify if adding new notification
    }
}
```

### Modern Spring Events (Loose Coupling)
```java
// Publisher doesn't know about listeners!
public class OrderService {
    private ApplicationEventPublisher publisher;

    public void createOrder() {
        // Business logic
        publisher.publishEvent(new OrderCreatedEvent());
        // Done! Listeners react automatically
    }
}
```

## Key Concepts

### 1. Publishing Events
```java
@Service
public class OrderService {
    private final ApplicationEventPublisher publisher;

    public void createOrder(Long id) {
        // Save order
        publisher.publishEvent(new OrderCreatedEvent(id));
    }
}
```

### 2. Listening to Events (Synchronous)
```java
@Component
public class EmailListener {
    @EventListener
    public void handleOrder(OrderCreatedEvent event) {
        // Send email (runs in same thread)
    }
}
```

### 3. Async Event Listeners
```java
@Component
public class AnalyticsListener {
    @Async  // Runs in separate thread
    @EventListener
    public void handleOrder(OrderCreatedEvent event) {
        // Process analytics (non-blocking)
    }
}
```

### 4. Event Execution Order
```java
@EventListener
@Order(1)  // Executes first
public void handleFirst(OrderCreatedEvent event) {}

@EventListener
@Order(2)  // Executes second
public void handleSecond(OrderCreatedEvent event) {}
```

### 5. Conditional Event Listening
```java
@EventListener(condition = "#event.amount > 1000")
public void handleLargeOrder(OrderCreatedEvent event) {
    // Only for orders > $1000
}
```

### 6. Transactional Events
```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleAfterCommit(OrderCreatedEvent event) {
    // Runs only after transaction commits
}
```

## Running the Application

```bash
./gradlew bootRun
```

## Testing

```bash
curl -X POST http://localhost:8086/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 123,
    "email": "customer@example.com",
    "amount": 99.99
  }'
```

Watch console output to see:
- Synchronous listeners execute in order
- Async listeners run in separate threads
- Event propagation

## Benefits of Spring Events

1. **Loose Coupling** - Publisher doesn't know listeners
2. **Scalability** - Add listeners without modifying publisher
3. **Async Support** - Non-blocking operations with @Async
4. **Testability** - Easy to test components independently
5. **Transaction Support** - @TransactionalEventListener
6. **Built-in** - No external dependencies

## Common Use Cases

- **Order Processing** - Email, SMS, analytics, inventory
- **User Registration** - Welcome email, analytics, audit log
- **Payment Processing** - Notifications, fraud detection, reporting
- **Audit Logging** - Track all system events
- **Cache Invalidation** - Clear cache on data changes

## Event Types

**Simple POJO (Modern):**
```java
public class OrderCreatedEvent {
    private final Long orderId;
    // Constructor, getters
}
```

**Extending ApplicationEvent (Legacy):**
```java
public class OrderEvent extends ApplicationEvent {
    public OrderEvent(Object source) {
        super(source);
    }
}
```

Modern approach: Use simple POJOs (no need to extend ApplicationEvent).

## Best Practices

1. **Use records for events** - Immutable, concise
2. **Async for slow operations** - Don't block main thread
3. **@Order for dependencies** - Control execution order
4. **@TransactionalEventListener** - For database consistency
5. **Condition for filtering** - Process only relevant events
