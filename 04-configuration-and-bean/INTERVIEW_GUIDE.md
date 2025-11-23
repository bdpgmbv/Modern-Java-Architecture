# @Configuration and @Bean Interview Guide

## Common Interview Questions & Answers

### 1. What's the difference between @Component and @Bean?

**Answer:**
"@Component and @Bean are both used to create Spring beans, but they serve different purposes:

**@Component:**
- **Class-level** annotation
- Used for classes **you own** (in your codebase)
- Enables **automatic detection** via component scanning
- Spring creates bean using the **default constructor**
- Used for: Services, Repositories, Controllers

**Example:**
```java
@Component
public class EmailService {
    // Spring auto-creates this bean
}
```

**@Bean:**
- **Method-level** annotation
- Used in **@Configuration** classes
- For **programmatic bean creation**
- Used for classes you **don't control** (third-party libraries)
- Allows **custom initialization** and **constructor parameters**

**Example:**
```java
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        // Custom initialization for third-party class
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }
}
```

**When to use each:**
- @Component: Your own service/repository classes
- @Bean: Third-party libraries, custom initialization, constructor parameters

**Interview Tip:** Give a concrete example like 'ObjectMapper from Jackson library - you can't add @Component to it, so you use @Bean.'"

---

### 2. What is @Configuration and how does it differ from @Component?

**Answer:**
"@Configuration is a specialized version of @Component that indicates a class contains bean definitions.

**Key differences:**

| Feature | @Configuration | @Component |
|---------|----------------|------------|
| **Purpose** | Bean definitions | Business logic |
| **@Bean methods** | Yes | No (discouraged) |
| **CGLIB proxy** | Yes | No |
| **Singleton guarantee** | Yes (for @Bean calls) | N/A |

**Most important difference - CGLIB proxy:**

```java
@Configuration  // CGLIB-proxied
public class AppConfig {
    @Bean
    public ServiceA serviceA() {
        return new ServiceA(serviceB());  // Returns singleton
    }

    @Bean
    public ServiceB serviceB() {
        return new ServiceB();
    }
}
```

With @Configuration, calling `serviceB()` returns the **same singleton instance**.
Without @Configuration, calling `serviceB()` creates a **new instance each time**!

**Interview Tip:** Explain that @Configuration ensures singleton behavior when @Bean methods call each other."

---

### 3. Explain @Primary and @Qualifier. When would you use them?

**Answer:**
"@Primary and @Qualifier solve the problem of **multiple bean candidates** for the same type.

**Scenario:** You have multiple implementations of an interface:

```java
public interface MessageService {}

@Component
public class EmailService implements MessageService {}

@Component
public class SmsService implements MessageService {}

@Component
public class PushService implements MessageService {}
```

**Problem:** Which one should Spring inject here?
```java
@Autowired
MessageService service;  // ERROR: Multiple beans of type MessageService!
```

**Solution 1: @Primary (Default Bean)**

```java
@Component
@Primary  // This is the DEFAULT
public class PushService implements MessageService {}

@Autowired
MessageService service;  // Injects PushService (because of @Primary)
```

**Solution 2: @Qualifier (Specific Bean)**

```java
@Autowired
@Qualifier("emailService")  // Specific bean by name
MessageService emailService;

@Autowired
@Qualifier("smsService")
MessageService smsService;
```

**Combined approach:**
```java
// Default implementation
@Component
@Primary
public class PushService implements MessageService {}

// Alternative implementations
@Component
public class EmailService implements MessageService {}

// Usage
@Autowired
MessageService defaultService;  // PushService (via @Primary)

@Autowired
@Qualifier("emailService")
MessageService emailService;  // EmailService (via @Qualifier)
```

**Interview Tip:** Mention that @Primary is for 80% of cases, @Qualifier for specific needs."

---

### 4. What is a FactoryBean? When would you use it?

**Answer:**
"FactoryBean is a **special bean that creates other beans**. It's used for complex bean creation that requires multi-step initialization.

**How it works:**
1. You implement the `FactoryBean<T>` interface
2. Spring calls `getObject()` to create the actual bean
3. The object returned by `getObject()` is what gets injected (not the FactoryBean itself)

**Example:**

```java
@Component
public class ConnectionFactoryBean implements FactoryBean<Connection> {

    @Override
    public Connection getObject() throws Exception {
        // Complex initialization
        Connection conn = new Connection();
        conn.setUrl("jdbc:...");
        conn.initialize();
        conn.testConnection();
        return conn;
    }

    @Override
    public Class<?> getObjectType() {
        return Connection.class;
    }

    @Override
    public boolean isSingleton() {
        return true;  // One instance
    }
}

// Usage
@Autowired
Connection connection;  // Injects the Connection, not the FactoryBean!
```

**When to use FactoryBean:**
1. **Multi-step initialization** - Complex setup process
2. **Different types** - Create different objects based on config
3. **Third-party integration** - Complex library setup (connection pools, HTTP clients)
4. **Lifecycle management** - Need init/destroy hooks
5. **Proxy creation** - Create proxy objects

**Real-world examples:**
- Database connection pools (HikariCP, C3P0)
- HTTP clients (RestTemplate, WebClient)
- JPA EntityManagerFactory
- Transaction managers

**Interview Tip:** Emphasize that FactoryBean is for **complex creation** - for simple cases, use @Bean."

---

### 5. How does Spring resolve dependencies when multiple beans of the same type exist?

**Answer:**
"Spring uses a specific resolution strategy:

**Resolution order:**

1. **@Primary** - If one bean is marked @Primary, use that
2. **@Qualifier** - If @Qualifier is specified, use that exact bean
3. **Bean name match** - If parameter name matches bean name, use that
4. **Fail** - If none of above, throw NoUniqueBeanDefinitionException

**Example demonstrating all strategies:**

```java
// Beans
@Component
@Primary
public class PushService implements MessageService {}

@Component("emailSvc")
public class EmailService implements MessageService {}

@Component
public class SmsService implements MessageService {}

// Resolution examples
@Component
public class NotificationController {

    // 1. @Primary - injects PushService
    @Autowired
    MessageService service;

    // 2. @Qualifier - injects EmailService
    @Autowired
    @Qualifier("emailSvc")
    MessageService emailService;

    // 3. Name matching - injects SmsService
    @Autowired
    MessageService smsService;  // Matches bean name "smsService"

    // 4. ERROR - multiple candidates, no clear match
    @Autowired
    MessageService messageService;  // Would fail without @Primary
}
```

**Best practice:**
- Use @Primary for the default/most common implementation
- Use @Qualifier when you need a specific implementation
- Use descriptive parameter names that match bean names

**Interview Tip:** Explain that Spring tries to be smart about bean resolution, but @Primary/@Qualifier give you explicit control."

---

### 6. Can you explain bean scopes? What's the default scope?

**Answer:**
"Bean scope defines the lifecycle and visibility of a bean.

**Default scope: SINGLETON** - One instance per Spring container.

**All scopes:**

| Scope | Description | Use Case |
|-------|-------------|----------|
| **singleton** | One instance per container (default) | Stateless services, repositories |
| **prototype** | New instance each time | Stateful objects, not shared |
| **request** | One per HTTP request (web) | Request-specific data |
| **session** | One per HTTP session (web) | User session data |
| **application** | One per ServletContext (web) | Application-wide data |
| **websocket** | One per WebSocket session | WebSocket connections |

**Examples:**

```java
// Singleton (default)
@Component
public class EmailService {
    // One instance for entire application
}

// Explicit singleton
@Component
@Scope("singleton")
public class EmailService {}

// Prototype - new instance each time
@Component
@Scope("prototype")
public class ShoppingCart {
    // New instance for each user
}

// Request scope
@Component
@Scope("request")
public class RequestContext {
    // New instance per HTTP request
}

// With @Bean
@Bean
@Scope("prototype")
public Connection connection() {
    return new Connection();
}
```

**Important:** Singleton beans are created at **startup**, prototype beans are created **on demand**.

**Interview Tip:** Mention that 95% of beans are singleton, and explain why (stateless, performance)."

---

### 7. What happens if you call a @Bean method directly within @Configuration?

**Answer:**
"This is a tricky question that tests understanding of @Configuration's CGLIB proxy!

**With @Configuration (CGLIB proxy):**

```java
@Configuration
public class AppConfig {

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

**Result:** `serviceB()` returns the **same singleton instance** because Spring intercepts the call via CGLIB proxy.

**Without @Configuration:**

```java
// Missing @Configuration - just @Component
@Component
public class AppConfig {

    @Bean
    public ServiceA serviceA() {
        return new ServiceA(serviceB());  // Direct method call
    }

    @Bean
    public ServiceB serviceB() {
        return new ServiceB();
    }
}
```

**Result:** `serviceB()` creates a **new instance** each time - violates singleton contract!

**How CGLIB proxy works:**
1. Spring creates a proxy subclass of your @Configuration class
2. When you call `serviceB()`, the proxy intercepts it
3. Proxy returns the singleton bean from ApplicationContext
4. This ensures singleton behavior

**Best practice:** Always use @Configuration for classes with @Bean methods!

**Interview Tip:** This demonstrates deep understanding of Spring's internals!"

---

### 8. How do you inject values from application.properties into @Bean methods?

**Answer:**
"There are multiple ways to inject configuration values:

**Method 1: @Value in @Configuration class**

```java
@Configuration
public class AppConfig {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.max-connections:10}")  // Default: 10
    private int maxConnections;

    @Bean
    public DataSource dataSource() {
        return new DataSource(dbUrl, maxConnections);
    }
}
```

**Method 2: @Value in @Bean method parameters**

```java
@Configuration
public class AppConfig {

    @Bean
    public DataSource dataSource(
        @Value("${db.url}") String dbUrl,
        @Value("${db.max-connections:10}") int maxConnections
    ) {
        return new DataSource(dbUrl, maxConnections);
    }
}
```

**Method 3: Environment object**

```java
@Configuration
public class AppConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        String dbUrl = env.getProperty("db.url");
        int maxConnections = env.getProperty("db.max-connections", Integer.class, 10);
        return new DataSource(dbUrl, maxConnections);
    }
}
```

**Method 4: @ConfigurationProperties (recommended for complex config)**

```java
@ConfigurationProperties(prefix = "db")
@Data
public class DatabaseProperties {
    private String url;
    private int maxConnections = 10;
}

@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
public class AppConfig {

    @Bean
    public DataSource dataSource(DatabaseProperties props) {
        return new DataSource(props.getUrl(), props.getMaxConnections());
    }
}
```

**Interview Tip:** Recommend @ConfigurationProperties for complex configuration, @Value for simple cases."

---

### 9. What's the difference between @Bean and @Autowired?

**Answer:**
"These are fundamentally different annotations:

**@Bean:**
- **Creates** beans
- Used in @Configuration classes
- Method-level annotation
- **Producer** of beans

```java
@Configuration
public class Config {
    @Bean  // CREATES a bean
    public EmailService emailService() {
        return new EmailService();
    }
}
```

**@Autowired:**
- **Injects** existing beans
- Used in any Spring-managed component
- Field/Constructor/Setter annotation
- **Consumer** of beans

```java
@Component
public class NotificationService {
    @Autowired  // INJECTS existing bean
    private EmailService emailService;
}
```

**Analogy:**
- @Bean = Factory that manufactures cars
- @Autowired = Person who requests a car

**Interview Tip:** Emphasize that @Bean creates, @Autowired consumes."

---

### 10. How would you create multiple beans of the same type with different configurations?

**Answer:**
"Use @Bean methods with different names:

```java
@Configuration
public class AppConfig {

    // Production database
    @Bean("prodDatabase")
    @Primary
    public DataSource productionDatabase() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://prod-server/db")
            .username("prod_user")
            .password("prod_pass")
            .build();
    }

    // Test database
    @Bean("testDatabase")
    public DataSource testDatabase() {
        return DataSourceBuilder.create()
            .url("jdbc:h2:mem:testdb")
            .username("sa")
            .password("")
            .build();
    }

    // Reporting database
    @Bean("reportingDatabase")
    public DataSource reportingDatabase() {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://reporting-server/db")
            .username("reporting_user")
            .password("reporting_pass")
            .build();
    }
}

// Usage
@Service
public class ProductService {

    private final DataSource dataSource;
    private final DataSource reportingDataSource;

    public ProductService(
        DataSource dataSource,  // @Primary bean (prodDatabase)
        @Qualifier("reportingDatabase") DataSource reportingDataSource
    ) {
        this.dataSource = dataSource;
        this.reportingDataSource = reportingDataSource;
    }
}
```

**Real-world example - Multiple RestTemplates:**

```java
@Configuration
public class RestTemplateConfig {

    @Bean("internalApi")
    public RestTemplate internalApiRestTemplate() {
        RestTemplate template = new RestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        // Configure for internal API
        return template;
    }

    @Bean("externalApi")
    public RestTemplate externalApiRestTemplate() {
        RestTemplate template = new RestTemplate();
        // Different configuration for external API
        return template;
    }
}
```

**Interview Tip:** This is a common real-world pattern - multiple databases, multiple API clients, etc."

---

## How to Present @Configuration/@Bean Knowledge in Interviews

### 1. When Discussing Spring Configuration
"In my projects, I use @Configuration classes to organize all bean definitions in one place. For example, I created a DatabaseConfiguration class with @Bean methods for DataSource, EntityManagerFactory, and TransactionManager. This separates infrastructure concerns from business logic."

### 2. When Discussing Third-Party Integration
"When integrating third-party libraries like Jackson's ObjectMapper or RestTemplate, I use @Bean methods in @Configuration classes. This allows me to customize the configuration - for example, configuring ObjectMapper with custom serializers or RestTemplate with connection timeouts and interceptors."

### 3. When Discussing Multiple Implementations
"For a messaging system, I implemented multiple MessageService providers (Email, SMS, Push). I used @Primary to mark the default implementation and @Qualifier for specific use cases. This pattern made the code flexible and testable."

### 4. When Discussing Complex Initialization
"For connection pooling, I implemented a FactoryBean that handles multi-step initialization - validating configuration, creating the pool, and testing connections. This encapsulates complexity and provides proper lifecycle management."

---

## Code Demonstration for Interviews

### Quick Example - All Concepts

```java
// Interface
public interface NotificationService {
    void send(String message);
}

// Implementation 1 - via @Component
@Component
public class EmailNotificationService implements NotificationService {
    public void send(String message) {
        // Send email
    }
}

// Implementation 2 - via @Bean
public class SmsNotificationService implements NotificationService {
    private final String apiKey;

    public SmsNotificationService(String apiKey) {
        this.apiKey = apiKey;
    }

    public void send(String message) {
        // Send SMS using API key
    }
}

// Configuration
@Configuration
public class NotificationConfig {

    @Value("${sms.api-key}")
    private String smsApiKey;

    @Bean
    @Primary  // Default implementation
    public NotificationService smsNotificationService() {
        return new SmsNotificationService(smsApiKey);
    }
}

// Usage
@Service
public class UserService {

    private final NotificationService defaultNotifier;
    private final NotificationService emailNotifier;

    public UserService(
        NotificationService defaultNotifier,  // SMS (via @Primary)
        @Qualifier("emailNotificationService") NotificationService emailNotifier
    ) {
        this.defaultNotifier = defaultNotifier;
        this.emailNotifier = emailNotifier;
    }
}
```

---

## Common Interview Mistakes to Avoid

### 1. Forgetting @Configuration
```java
// WRONG
public class AppConfig {
    @Bean
    public Service service() {}
}

// CORRECT
@Configuration
public class AppConfig {
    @Bean
    public Service service() {}
}
```

### 2. Using Both @Component and @Bean
```java
// WRONG - creates two beans!
@Component
public class EmailService {}

@Configuration
public class Config {
    @Bean
    public EmailService emailService() {
        return new EmailService();
    }
}
```

### 3. Not Handling Multiple Implementations
```java
// WRONG - Spring doesn't know which to inject
@Component
public class Service1 implements MyInterface {}

@Component
public class Service2 implements MyInterface {}

@Autowired
MyInterface service;  // ERROR!

// CORRECT - use @Primary or @Qualifier
```

---

## Key Points to Remember

1. **@Component** for your classes, **@Bean** for third-party or custom init
2. **@Configuration** is CGLIB-proxied to ensure singleton behavior
3. **@Primary** for default bean, **@Qualifier** for specific beans
4. **FactoryBean** for complex multi-step bean creation
5. **@Value** for injecting properties into @Bean methods
6. **Default scope is SINGLETON** - one instance per container
7. **@Bean method name** becomes the bean name (unless specified)

---

## Final Interview Strategy

**DO:**
- Explain @Component vs @Bean clearly
- Give real examples (ObjectMapper, RestTemplate, DataSource)
- Demonstrate understanding of @Primary and @Qualifier
- Explain when to use FactoryBean

**DON'T:**
- Confuse @Bean (creates) with @Autowired (injects)
- Forget to mention @Configuration for @Bean methods
- Ignore the CGLIB proxy behavior
- Overcomplicate - start simple, add details if asked

---

**Remember:** @Configuration and @Bean are fundamental to Spring. Mastering them demonstrates understanding of dependency injection, bean lifecycle, and Spring's IoC container!
