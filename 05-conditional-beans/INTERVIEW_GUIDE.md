# Conditional Beans Interview Guide

## Key Interview Questions

### 1. What is @ConditionalOnProperty and when would you use it?

**Answer:**
"@ConditionalOnProperty creates a bean only if a property matches a specific value. It's perfect for feature flags and configuration-based bean selection.

```java
@Service
@ConditionalOnProperty(name = "cache.type", havingValue = "redis")
public class RedisCacheService implements CacheService {}

@Service
@ConditionalOnProperty(name = "cache.type", havingValue = "memory", matchIfMissing = true)
public class MemoryCacheService implements CacheService {}
```

**Use cases:**
- Feature toggles
- Multiple implementations based on config
- Default fallbacks with `matchIfMissing = true`

**Example:** Switch between Redis and in-memory cache without code changes."

---

### 2. Explain @ConditionalOnBean vs @ConditionalOnMissingBean

**Answer:**
"These create beans based on the presence or absence of other beans.

**@ConditionalOnBean** - Create only if another bean exists:
```java
@Bean
@ConditionalOnBean(DataSource.class)
public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    // Only created if DataSource bean exists
}
```

**@ConditionalOnMissingBean** - Create only if bean doesn't exist (fallback):
```java
@Bean
@ConditionalOnMissingBean(DataSource.class)
public DataSource defaultDataSource() {
    // Created only if NO other DataSource exists
}
```

**Use case:** Auto-configuration with custom overrides - Spring Boot uses this extensively."

---

### 3. What's the difference between @Profile and @ConditionalOnProperty?

**Answer:**
"Both control bean creation, but for different purposes:

**@Profile** - Environment-based (dev/test/prod):
```java
@Bean
@Profile("dev")
public DataSource devDataSource() {
    return new H2DataSource();
}

@Bean
@Profile("prod")
public DataSource prodDataSource() {
    return new PostgresDataSource();
}
```
Activated with: `--spring.profiles.active=prod`

**@ConditionalOnProperty** - Property-based (feature flags):
```java
@Service
@ConditionalOnProperty(name = "features.analytics", havingValue = "true")
public class AnalyticsService {}
```

**Key difference:** Profiles are for environments, properties are for features/configuration."

---

### 4. How do you implement feature flags in Spring Boot?

**Answer:**
"Use @ConditionalOnProperty:

```java
@Service
@ConditionalOnProperty(name = "features.new-checkout", havingValue = "true", matchIfMissing = false)
public class NewCheckoutService implements CheckoutService {}

@Service
@ConditionalOnProperty(name = "features.new-checkout", havingValue = "false", matchIfMissing = true)
public class OldCheckoutService implements CheckoutService {}
```

**application.properties:**
```properties
features.new-checkout=true
```

**Benefits:**
- Toggle features without code deployment
- A/B testing
- Gradual rollout
- Quick rollback

**Production pattern:** Use external config (Spring Cloud Config, Consul) for runtime changes."

---

### 5. Explain matchIfMissing in @ConditionalOnProperty

**Answer:**
"`matchIfMissing` controls bean creation when property doesn't exist:

```java
// Created if property=redis OR property doesn't exist
@ConditionalOnProperty(name = "cache", havingValue = "redis", matchIfMissing = true)

// Created ONLY if property=redis
@ConditionalOnProperty(name = "cache", havingValue = "redis", matchIfMissing = false)
```

**Use case - Default fallback:**
```java
@Service
@ConditionalOnProperty(name = "cache.type", havingValue = "memory", matchIfMissing = true)
public class MemoryCacheService {
    // Default cache if cache.type is not configured
}
```

**Best practice:** Use `matchIfMissing = true` for default implementations."

---

### 6. How does Spring Boot auto-configuration use conditional beans?

**Answer:**
"Spring Boot's auto-configuration is built entirely on conditional beans:

```java
@Configuration
@ConditionalOnClass(DataSource.class)  // Only if DataSource class exists
public class DataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean  // Only if you haven't defined DataSource
    public DataSource dataSource() {
        // Auto-configured DataSource
    }
}
```

**Pattern:**
1. Check if library is on classpath (@ConditionalOnClass)
2. Check if user hasn't defined bean (@ConditionalOnMissingBean)
3. Create default configuration

**Example:** `spring-boot-starter-data-jpa` auto-configures EntityManager only if you don't provide one."

---

### 7. Can you create custom conditional annotations?

**Answer:**
"Yes! Implement Condition interface:

```java
public class WindowsCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(WindowsCondition.class)
public @interface ConditionalOnWindows {}

// Usage
@Bean
@ConditionalOnWindows
public String windowsSpecificBean() {
    return "Windows-specific configuration";
}
```

**Use cases:** Complex conditions not covered by built-in annotations."

---

## How to Present in Interviews

**Discussing Configuration Management:**
"In production, I use conditional beans for feature flags. For example, when rolling out a new payment processor, I use @ConditionalOnProperty to toggle between old and new implementations without deployment."

**Discussing Environment Setup:**
"I use @Profile for environment-specific beans - H2 for dev, PostgreSQL for prod. This ensures developers can run locally without external dependencies."

**Discussing Auto-Configuration:**
"I created custom auto-configuration using @ConditionalOnMissingBean, allowing users to override defaults. This pattern is used extensively in Spring Boot starters."

---

## Key Points
1. **@ConditionalOnProperty** - property-based beans
2. **@ConditionalOnBean/Missing** - bean presence/absence
3. **@Profile** - environment-based
4. **matchIfMissing** - default fallback
5. **Feature flags** - runtime toggling
6. **Auto-configuration** - Spring Boot pattern
