# 💼 Interview Guide: Constructor vs Field Injection

## 🎤 How to Present This Skill

**Resume:**
> "Implemented Spring Boot services using constructor injection for improved testability and immutability, replacing legacy field injection patterns"

**Interview:**
> "I use constructor injection instead of field injection because it allows fields to be final, makes dependencies explicit, and significantly simplifies unit testing. I can create service instances with mocks without needing Spring context or reflection."

---

## ❓ Common Interview Questions

### Q1: What types of dependency injection does Spring support?

**Answer:**
"Spring supports three types:
1. **Constructor injection** (recommended) - dependencies via constructor
2. **Setter injection** - dependencies via setter methods
3. **Field injection** (deprecated) - `@Autowired` on fields

Constructor injection is recommended because fields can be final, testing is easier, and dependencies are explicit."

---

### Q2: Why is constructor injection better than field injection?

**Answer:**
"Constructor injection has several advantages:

1. **Immutability** - Fields can be final, ensuring thread safety
2. **Testability** - Easy to create instances with mock dependencies without Spring
3. **Explicit dependencies** - All required dependencies visible in constructor
4. **Fail-fast** - Missing dependencies detected at application startup, not runtime
5. **No reflection** - Simpler, more maintainable code

For example, testing with constructor injection:
```java
UserRepository mockRepo = mock(UserRepository.class);
UserService service = new UserService(mockRepo); // Easy!
```

With field injection, you need `ReflectionTestUtils` or load entire Spring context."

---

### Q3: Do you need @Autowired on constructors?

**Answer:**
"No, since Spring 4.3, `@Autowired` is optional when there's only one constructor. Spring automatically detects and uses it for dependency injection. This makes code cleaner:

```java
@Service
public class UserService {
    private final UserRepository repository;

    // No @Autowired needed!
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```"

---

### Q4: How do you test a service with constructor injection?

**Answer:**
"It's very simple - just create a mock and pass it to the constructor:

```java
@Test
void testCreateUser() {
    // Create mock
    UserRepository mockRepo = mock(UserRepository.class);
    when(mockRepo.save(any())).thenReturn(new User(1L, \"test\", \"test@example.com\"));

    // Create service with mock - no Spring needed!
    UserService service = new UserService(mockRepo);

    // Test
    User result = service.createUser(\"test\", \"test@example.com\");
    assertNotNull(result);
    verify(mockRepo).save(any());
}
```

No Spring context, no reflection - just clean, fast tests."

---

### Q5: What are the problems with field injection?

**Answer:**
"Field injection has several issues:

1. **No immutability** - Can't make fields final
2. **Hard to test** - Need `ReflectionTestUtils` or `@SpringBootTest`
3. **Hidden dependencies** - Not obvious what's required
4. **NullPointer risk** - Can create instance without dependencies
5. **Violates SOLID** - Specifically Dependency Inversion Principle

Example problem:
```java
@Service
public class UserService {
    @Autowired
    private UserRepository repository; // Can't be final!

    // Can create instance - repository will be null!
    public UserService() { }
}
```"

---

### Q6: When would you use setter injection?

**Answer:**
"Setter injection is useful for optional dependencies that have reasonable defaults:

```java
@Service
public class EmailService {
    private String fromAddress = \"noreply@example.com\"; // Default

    @Autowired(required = false) // Optional
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
}
```

However, in most cases, constructor injection with default values is cleaner."

---

### Q7: How do you handle circular dependencies?

**Answer:**
"Circular dependencies indicate a design problem. Solutions:

1. **Refactor** - Extract common functionality to a third service (best)
2. **Setter injection** - One of the services uses setter injection
3. **@Lazy** - Lazy-initialize one of the dependencies

Best approach:
```java
// Bad: A depends on B, B depends on A

// Good: Extract shared logic to C
@Service
class ServiceA {
    private final ServiceC serviceC;
}

@Service
class ServiceB {
    private final ServiceC serviceC;
}

@Service
class ServiceC {
    // Shared logic
}
```"

---

### Q8: Can you have multiple constructors with constructor injection?

**Answer:**
"Yes, but only one constructor can be used for dependency injection. If you have multiple constructors, you must use `@Autowired` to specify which one Spring should use:

```java
@Service
public class UserService {
    private final UserRepository repository;
    private final EmailService emailService;

    @Autowired // Required when multiple constructors exist
    public UserService(UserRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    // Constructor for testing or other purposes
    public UserService(UserRepository repository) {
        this(repository, null);
    }
}
```"

---

## 💡 Code Review Scenario

**Interviewer shows you:**
```java
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailService emailService;

    public void createOrder(Order order) {
        orderRepository.save(order);
        paymentService.process(order);
        emailService.send(order);
    }
}
```

**What would you improve?**

**Your Answer:**
"I would refactor to use constructor injection for better testability and immutability:

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;

    // Constructor injection - @Autowired optional
    public OrderService(
        OrderRepository orderRepository,
        PaymentService paymentService,
        EmailService emailService
    ) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    public void createOrder(Order order) {
        orderRepository.save(order);
        paymentService.process(order);
        emailService.send(order);
    }
}
```

Benefits:
- ✅ All fields are final (immutable)
- ✅ Easy to test with mocks
- ✅ Clear dependencies
- ✅ Can't create instance without dependencies"

---

## 🎯 What Interviewers Look For

### ✅ Good Signals:
- Uses constructor injection by default
- Can explain testing benefits
- Knows `@Autowired` is optional (Spring 4.3+)
- Understands immutability importance
- Can compare different injection types

### ❌ Red Flags:
- "Field injection is fine, it's easier"
- Doesn't know the testing difference
- Uses `@Autowired` everywhere unnecessarily
- Can't explain why final fields matter

---

## 📊 Quick Reference

| Aspect | Field Injection | Constructor Injection |
|--------|----------------|----------------------|
| Syntax | `@Autowired private Repo repo;` | `private final Repo repo;` + constructor |
| Immutability | ❌ No | ✅ Yes (final) |
| Testing | ❌ Hard (reflection) | ✅ Easy (new) |
| Dependencies | ❌ Hidden | ✅ Explicit |
| Safety | ❌ Can be null | ✅ Never null |
| Recommended | ❌ No | ✅ Yes |

---

## 🎓 Pro Tips

1. **Always use constructor injection** in new code
2. **Make fields final** for immutability
3. **@Autowired is optional** on single constructor (Spring 4.3+)
4. **Circular dependencies = design smell** - refactor instead
5. **Testing should be easy** - if it's not, your DI is wrong

---

## 🚀 Demonstration Tips

**In live coding:**
1. Show field injection service
2. Show constructor injection service
3. Compare test files side-by-side
4. Explain why constructor is cleaner

**Key points to emphasize:**
- "See how I can `new UserService(mockRepo)` with constructor injection?"
- "With field injection, I need `ReflectionTestUtils` - much more complex"
- "Final fields ensure thread safety and immutability"
- "Dependencies are explicit in the constructor signature"

---

**Remember:** Constructor injection is not just a preference - it's a Spring Boot best practice!
