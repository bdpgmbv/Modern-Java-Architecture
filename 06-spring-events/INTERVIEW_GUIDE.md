# Spring Events Interview Guide

## Key Interview Questions

### 1. What are Spring Events and why use them?

**Answer:**
"Spring Events enable event-driven architecture within Spring applications, providing loose coupling between components.

**Problem they solve:**
```java
// Tight coupling - OrderService knows all dependencies
@Service
public class OrderService {
    @Autowired EmailService emailService;
    @Autowired SmsService smsService;
    @Autowired AnalyticsService analyticsService;

    public void createOrder() {
        // ... business logic
        emailService.send();
        smsService.send();
        analyticsService.track();
        // Must modify for each new service!
    }
}
```

**Solution with Events:**
```java
// Loose coupling - OrderService doesn't know listeners
@Service
public class OrderService {
    @Autowired ApplicationEventPublisher publisher;

    public void createOrder() {
        // ... business logic
        publisher.publishEvent(new OrderCreatedEvent());
        // Done! Listeners react automatically
    }
}

// Add listeners without modifying OrderService
@Component
public class EmailListener {
    @EventListener
    public void handle(OrderCreatedEvent event) {
        // Send email
    }
}
```

**Benefits:**
- Loose coupling
- Easy to add new features
- Better testability
- Async support
"

---

### 2. Explain @EventListener vs implementing ApplicationListener

**Answer:**
"Two ways to listen to events:

**Old way - ApplicationListener interface:**
```java
@Component
public class EmailListener implements ApplicationListener<OrderCreatedEvent> {
    @Override
    public void onApplicationEvent(OrderCreatedEvent event) {
        // Handle event
    }
}
```

**Modern way - @EventListener:**
```java
@Component
public class EmailListener {
    @EventListener
    public void handleOrder(OrderCreatedEvent event) {
        // Handle event - cleaner!
    }
}
```

**Why @EventListener is better:**
- Less boilerplate
- Method name is flexible
- Multiple events in one class
- Support for SpEL conditions
- Can listen to multiple event types

**Recommended:** Use @EventListener in modern applications."

---

### 3. What's the difference between sync and async event listeners?

**Answer:**
"Synchronous listeners block the publisher, async listeners don't.

**Synchronous (default):**
```java
@EventListener
public void handleOrder(OrderCreatedEvent event) {
    sendEmail();  // Publisher waits for this
}
```
- Runs in same thread as publisher
- Publisher waits for completion
- Use for: Critical operations, transaction-bound

**Asynchronous (@Async):**
```java
@Async
@EventListener
public void handleOrder(OrderCreatedEvent event) {
    processAnalytics();  // Publisher doesn't wait
}
```
- Runs in separate thread
- Publisher continues immediately
- Use for: Slow operations, notifications, analytics

**Setup for @Async:**
```java
@SpringBootApplication
@EnableAsync
public class Application {}
```

**Real example:**
```java
@EventListener  // SYNC - must complete before response
public void updateInventory(OrderEvent e) {}

@Async
@EventListener  // ASYNC - don't block response
public void sendAnalytics(OrderEvent e) {}
```
"

---

### 4. Explain @TransactionalEventListener

**Answer:**
"`@TransactionalEventListener` executes listener only at specific transaction phases.

**Problem:** Event published but transaction rolls back:
```java
@Service
public class OrderService {
    @Transactional
    public void createOrder() {
        saveOrder();  // DB transaction
        publisher.publishEvent(new OrderEvent());
        // If exception here, order not saved but event already sent!
    }
}
```

**Solution:**
```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleOrder(OrderEvent event) {
    // Only runs if transaction commits successfully
    sendEmail();
}
```

**Phases:**
- `AFTER_COMMIT` - After successful commit (most common)
- `AFTER_ROLLBACK` - After rollback
- `AFTER_COMPLETION` - After commit or rollback
- `BEFORE_COMMIT` - Before commit

**Use case:** Sending emails/notifications only if database save succeeds."

---

### 5. How do you control event listener execution order?

**Answer:**
"Use @Order annotation:

```java
@Component
public class OrderListeners {

    @EventListener
    @Order(1)  // Executes FIRST
    public void validateOrder(OrderEvent event) {
        // Validation must run first
    }

    @EventListener
    @Order(2)  // Executes SECOND
    public void updateInventory(OrderEvent event) {
        // After validation
    }

    @EventListener
    @Order(3)  // Executes THIRD
    public void sendConfirmation(OrderEvent event) {
        // After inventory update
    }
}
```

**Important:** @Order only works for synchronous listeners! Async listeners run independently.

**Best practice:** Avoid dependencies between listeners when possible."

---

### 6. Can you filter events with conditions?

**Answer:**
"Yes, using SpEL expressions:

```java
// Only handle large orders
@EventListener(condition = "#event.amount > 1000")
public void handleLargeOrder(OrderEvent event) {
    notifyManager();
}

// Only handle specific status
@EventListener(condition = "#event.status == 'PREMIUM'")
public void handlePremiumOrder(OrderEvent event) {
    priorityProcessing();
}

// Complex conditions
@EventListener(condition = "#event.amount > 1000 and #event.customer.vip")
public void handleVipLargeOrder(OrderEvent event) {
    specialProcessing();
}
```

**Benefits:**
- Filter at framework level
- Cleaner listener code
- Better performance (listener not called if condition false)
"

---

### 7. How do Spring Events work internally?

**Answer:**
"Spring uses the ApplicationEventMulticaster:

**Flow:**
1. Publisher calls `publisher.publishEvent(event)`
2. Spring gets `ApplicationEventMulticaster` from context
3. Multicaster finds all listeners for that event type
4. For sync listeners: Calls each in same thread
5. For async listeners: Submits to thread pool
6. Returns control to publisher

**Multicaster resolution:**
- Checks listener parameter types
- Matches event type hierarchy
- Applies @Order for sync listeners
- Evaluates SpEL conditions

**Performance:** Uses caching to avoid repeated type resolution.

**Interview Tip:** Shows understanding of Spring internals."

---

## How to Present in Interviews

**Discussing Microservices:**
"In our order processing system, I used Spring Events to decouple components. When an order is created, we publish OrderCreatedEvent. Multiple listeners handle email, inventory, analytics - all without tight coupling. This made the system more maintainable and testable."

**Discussing Async Processing:**
"For performance, I use @Async event listeners for non-critical operations like analytics and notifications. This ensures our API responds quickly while background tasks process asynchronously."

**Discussing Transaction Safety:**
"I use @TransactionalEventListener to ensure emails are only sent after database commits succeed. This prevents sending confirmation emails for failed orders."

---

## Common Mistakes

**1. Forgetting @EnableAsync:**
```java
@Async  // Doesn't work without @EnableAsync on main class!
@EventListener
public void handle(Event e) {}
```

**2. Returning values from void listeners:**
```java
@EventListener
public String handle(Event e) {  // Return value ignored!
    return "result";
}
```

**3. Using @Order with @Async:**
```java
@Async
@EventListener
@Order(1)  // @Order has no effect with @Async!
public void handle(Event e) {}
```

---

## Key Points
1. **Events enable loose coupling**
2. **@EventListener is modern approach**
3. **@Async for non-blocking**
4. **@TransactionalEventListener for DB consistency**
5. **@Order for execution sequence**
6. **SpEL conditions for filtering**
