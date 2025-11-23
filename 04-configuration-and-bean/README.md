# Project 4: @Configuration and @Bean

## Overview

This project demonstrates **@Configuration** and **@Bean** annotations in Spring Boot - essential for programmatic bean creation and dependency configuration.

## What are @Configuration and @Bean?

### @Configuration
- Class-level annotation marking a class as a source of bean definitions
- Indicates this class contains infrastructure/configuration setup
- CGLIB-proxied to ensure singleton behavior for @Bean methods

### @Bean
- Method-level annotation for programmatic bean creation
- Alternative to @Component for classes you can't or don't want to annotate
- Allows custom initialization logic and constructor parameters

## @Component vs @Bean: When to Use Each?

### Use @Component when:
- You **OWN** the class (it's in your codebase)
- No custom initialization needed
- Default constructor is sufficient
- You want automatic bean detection via component scanning

```java
@Component
public class EmailMessageService implements MessageService {
    // Spring auto-detects and creates this bean
}
```

### Use @Bean when:
- Class is from a **third-party library** (can't add @Component)
- Need **constructor parameters**
- Need **custom initialization logic**
- Need **conditional bean creation**
- Want to create **multiple beans from same class**

```java
@Configuration
public class AppConfiguration {

    @Bean
    public MessageService smsService() {
        // Custom initialization with parameters
        return new SmsMessageService("Twilio");
    }
}
```

## Key Concepts Demonstrated

### 1. Basic @Bean Creation

```java
@Configuration
public class AppConfiguration {

    /**
     * Method name becomes bean name: "smsMessageService"
     */
    @Bean
    public MessageService smsMessageService() {
        return new SmsMessageService("Twilio");
    }
}
```

**What happens:**
1. Spring detects @Configuration class
2. Calls @Bean methods during startup
3. Stores returned objects as beans in ApplicationContext
4. Bean name = method name (unless specified otherwise)

### 2. @Primary - Default Bean Selection

When multiple implementations exist, @Primary marks the default:

```java
@Configuration
public class AppConfiguration {

    @Bean
    public MessageService smsService() {
        return new SmsMessageService("Twilio");
    }

    @Bean
    @Primary  // This is the DEFAULT
    public MessageService pushService() {
        return new PushMessageService("MyApp", false);
    }
}

// In controller
@Autowired
private MessageService service;  // Injects pushService (because of @Primary)
```

### 3. @Qualifier - Specific Bean Selection

Use @Qualifier to inject a specific bean by name:

```java
@Autowired
public ConfigController(
    MessageService defaultService,  // @Primary bean

    @Qualifier("smsMessageService")
    MessageService smsService,  // Specific bean

    @Qualifier("emailMessageService")
    MessageService emailService  // Another specific bean
) {
    // ...
}
```

### 4. Custom Bean Names

```java
@Bean(name = "customServiceName")
public MessageService service() {
    return new SmsMessageService("Twilio");
}

// Or multiple names
@Bean({"service1", "service2"})
public MessageService multiNamedService() {
    return new SmsMessageService("Twilio");
}
```

### 5. @Bean with Dependencies

@Bean methods can have parameters - Spring auto-injects them:

```java
@Bean
public String applicationVersion() {
    return "1.0.0";
}

@Bean
public String welcomeMessage(String applicationVersion) {
    // Spring automatically injects applicationVersion bean
    return "Welcome v" + applicationVersion;
}
```

### 6. @Value in @Configuration

Inject properties from application.properties:

```java
@Configuration
public class AppConfiguration {

    @Value("${app.name}")
    private String appName;

    @Value("${sms.provider:Twilio}")  // Default: Twilio
    private String smsProvider;

    @Bean
    public MessageService smsService() {
        return new SmsMessageService(smsProvider);
    }
}
```

### 7. FactoryBean - Advanced Bean Creation

For complex bean creation with lifecycle management:

```java
@Component
public class DatabaseConnectionFactoryBean implements FactoryBean<DatabaseConnection> {

    @Override
    public DatabaseConnection getObject() throws Exception {
        // Complex initialization logic
        validateConfiguration();
        initializePool();
        return new DatabaseConnection(...);
    }

    @Override
    public Class<?> getObjectType() {
        return DatabaseConnection.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
```

**When to use FactoryBean:**
- Complex multi-step initialization
- Different types based on configuration
- Lifecycle hooks (init, destroy)
- Creating proxies
- Third-party library objects with complex setup

**How it works:**
1. Spring detects the FactoryBean
2. Calls `getObject()` to create the actual bean
3. The returned object is what gets injected (not the FactoryBean itself)
4. To inject the FactoryBean, use: `@Autowired &beanName`

## Project Structure

```
04-configuration-and-bean/
├── src/main/java/com/example/config/
│   ├── ConfigurationAndBeanApplication.java  # Main application
│   ├── config/
│   │   └── AppConfiguration.java             # @Configuration with @Bean methods
│   ├── service/
│   │   ├── MessageService.java               # Interface
│   │   ├── EmailMessageService.java          # @Component implementation
│   │   ├── SmsMessageService.java            # @Bean implementation
│   │   └── PushMessageService.java           # @Bean + @Primary
│   ├── factory/
│   │   ├── DatabaseConnection.java           # Complex object
│   │   └── DatabaseConnectionFactoryBean.java # FactoryBean implementation
│   └── controller/
│       └── ConfigDemoController.java         # Demonstrates all concepts
├── src/main/resources/
│   └── application.properties
├── build.gradle.kts
├── README.md
└── INTERVIEW_GUIDE.md
```

## Running the Application

### 1. Build and Run
```bash
./gradlew bootRun
```

Application starts on `http://localhost:8084`

### 2. Test the Endpoints

**Get default service (via @Primary):**
```bash
curl http://localhost:8084/api/config/default
```

**Get SMS service (via @Qualifier):**
```bash
curl http://localhost:8084/api/config/sms
```

**Get Email service (via @Component):**
```bash
curl http://localhost:8084/api/config/email
```

**Get Notification service (custom bean name):**
```bash
curl http://localhost:8084/api/config/notification
```

**Get Database connection (via FactoryBean):**
```bash
curl http://localhost:8084/api/config/database
```

**Get welcome message:**
```bash
curl http://localhost:8084/api/config/welcome
```

**Get all MessageService beans:**
```bash
curl http://localhost:8084/api/config/all-services
```

**Compare bean creation methods:**
```bash
curl http://localhost:8084/api/config/bean-comparison
```

## @Component vs @Bean vs FactoryBean Comparison

| Feature | @Component | @Bean | FactoryBean |
|---------|------------|-------|-------------|
| **Use Case** | Own classes | Third-party or custom init | Complex creation |
| **Location** | On class | In @Configuration | Implements interface |
| **Detection** | Component scan | Explicit method | Component scan |
| **Initialization** | Constructor | Method body | getObject() method |
| **Parameters** | Limited (DI only) | Full control | Full control |
| **Complexity** | Simple | Medium | High |
| **Best For** | Services, Repositories | Config objects, Library beans | Connection pools, Proxies |

## Common Patterns

### Pattern 1: Multiple Implementations with @Primary

```java
// Interface
public interface MessageService { }

// Implementation 1 (via @Component)
@Component
public class EmailService implements MessageService { }

// Implementation 2 (via @Bean + @Primary)
@Configuration
public class Config {
    @Bean
    @Primary
    public MessageService smsService() {
        return new SmsService();
    }
}

// Usage
@Autowired
MessageService service;  // Injects smsService (because of @Primary)

@Autowired
@Qualifier("emailService")
MessageService emailService;  // Injects emailService specifically
```

### Pattern 2: Conditional Bean Creation

```java
@Configuration
public class Config {

    @Bean
    @ConditionalOnProperty(name = "feature.sms.enabled", havingValue = "true")
    public MessageService smsService() {
        return new SmsService();
    }
}
```

### Pattern 3: Environment-Specific Beans

```java
@Configuration
public class Config {

    @Value("${spring.profiles.active:dev}")
    private String profile;

    @Bean
    public MessageService messageService() {
        if ("prod".equals(profile)) {
            return new ProductionService();
        } else {
            return new DevelopmentService();
        }
    }
}
```

### Pattern 4: FactoryBean for Complex Setup

```java
@Component
public class HttpClientFactoryBean implements FactoryBean<HttpClient> {

    @Override
    public HttpClient getObject() {
        // Complex setup
        return HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .executor(Executors.newFixedThreadPool(5))
            .followRedirects(Redirect.NORMAL)
            .build();
    }

    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }
}
```

## Important Notes

### 1. @Configuration is CGLIB-Proxied

```java
@Configuration
public class Config {

    @Bean
    public ServiceA serviceA() {
        return new ServiceA(serviceB());  // Calls @Bean method
    }

    @Bean
    public ServiceB serviceB() {
        return new ServiceB();
    }
}
```

**With @Configuration:** `serviceB()` call returns the same singleton instance.
**Without @Configuration:** `serviceB()` call creates a new instance each time!

Always use `@Configuration` for bean definition classes!

### 2. Bean Names

Default bean names:
- `@Component`: Uncapitalized class name (`EmailMessageService` → `emailMessageService`)
- `@Bean`: Method name (`smsMessageService()` → `smsMessageService`)

Custom names:
- `@Component("customName")`
- `@Bean(name = "customName")`

### 3. @Autowired is Optional in Constructors

```java
// Modern way (Spring 4.3+)
@Component
public class MyService {
    private final Dependency dependency;

    // @Autowired is implicit!
    public MyService(Dependency dependency) {
        this.dependency = dependency;
    }
}
```

### 4. Circular Dependencies

Avoid circular dependencies:
```java
// BAD - circular dependency
@Bean
public ServiceA serviceA(ServiceB serviceB) { }

@Bean
public ServiceB serviceB(ServiceA serviceA) { }
```

Solutions:
- Refactor to remove circular dependency
- Use `@Lazy`
- Use setter injection instead of constructor

## Best Practices

### 1. Prefer Constructor Injection
```java
// Good
@Component
public class MyService {
    private final Dependency dependency;

    public MyService(Dependency dependency) {
        this.dependency = dependency;
    }
}
```

### 2. Use @Primary for Default Implementation
```java
@Bean
@Primary
public MessageService defaultService() {
    return new PushMessageService();
}
```

### 3. Use @Qualifier for Multiple Implementations
```java
@Autowired
@Qualifier("smsService")
private MessageService smsService;
```

### 4. Keep @Configuration Classes Focused
```java
// Good - focused configuration
@Configuration
public class DatabaseConfiguration { }

@Configuration
public class SecurityConfiguration { }

// Bad - everything in one class
@Configuration
public class ApplicationConfiguration { }
```

### 5. Use FactoryBean for Complex Creation Only
Don't use FactoryBean for simple objects - use @Bean instead.

## When to Use What?

### Use @Component when:
- Simple service/repository classes
- Classes you own
- No special initialization needed

### Use @Bean when:
- Third-party library classes
- Need constructor parameters from config
- Multiple implementations with @Primary/@Qualifier
- Conditional bean creation

### Use FactoryBean when:
- Multi-step complex initialization
- Creating proxies
- Connection pools
- Resource management (init/destroy)

## Common Mistakes

### 1. Forgetting @Configuration
```java
// WRONG - missing @Configuration
public class Config {
    @Bean
    public Service service() { }
}

// CORRECT
@Configuration
public class Config {
    @Bean
    public Service service() { }
}
```

### 2. Using @Component with @Bean
```java
// WRONG - don't mix @Component with @Bean
@Component
public class Service { }

@Configuration
public class Config {
    @Bean
    public Service service() {  // Conflict!
        return new Service();
    }
}
```

### 3. Not Using @Primary with Multiple Implementations
```java
// If you have multiple implementations, mark one as @Primary
// or always use @Qualifier
```

## Learning Resources

- **Spring Documentation**: https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans
- **Bean Definition**: Understanding Spring IoC container
- **FactoryBean**: Advanced bean creation patterns

---

**Key Takeaway**: Use **@Component** for your own classes, **@Bean** for third-party or complex initialization, and **FactoryBean** for multi-step complex bean creation. Understanding when to use each is crucial for clean Spring architecture!
