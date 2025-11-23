# Project 5: Conditional Beans

## Overview
Demonstrates Spring Boot's conditional bean creation - create beans based on properties, profiles, or other beans.

## Key Concepts

### 1. @ConditionalOnProperty
Create bean only if a property matches a value:

```java
@Service
@ConditionalOnProperty(name = "cache.type", havingValue = "redis")
public class RedisCacheService implements CacheService {
    // Created only if cache.type=redis
}

@Service
@ConditionalOnProperty(name = "cache.type", havingValue = "memory", matchIfMissing = true)
public class MemoryCacheService implements CacheService {
    // Created if cache.type=memory OR property is missing
}
```

### 2. @ConditionalOnBean / @ConditionalOnMissingBean
Create bean based on presence/absence of other beans:

```java
@Bean
@ConditionalOnMissingBean(DataProcessor.class)
public DataProcessor defaultProcessor() {
    // Created only if NO other DataProcessor exists (fallback bean)
}

@Bean
@ConditionalOnBean(name = "devConfig")
public String devLogger() {
    // Created only if devConfig bean exists
}
```

### 3. @Profile
Create bean only in specific environments:

```java
@Bean
@Profile("dev")
public String devConfig() {
    // Only in dev profile
}

@Bean
@Profile("prod")
public String prodConfig() {
    // Only in prod profile
}

@Bean
@Profile({"dev", "test"})
public String debugMode() {
    // In dev OR test profiles
}
```

### 4. Feature Flags
Toggle features via properties:

```java
@Service
@ConditionalOnProperty(name = "features.analytics", havingValue = "true")
public class AnalyticsFeature {
    // Created only if features.analytics=true
}
```

## Running the Application

```bash
./gradlew bootRun
```

## Testing Endpoints

```bash
curl http://localhost:8085/api/conditional/cache
curl http://localhost:8085/api/conditional/features
curl http://localhost:8085/api/conditional/all-beans
```

## Configuration Examples

**application.properties:**
```properties
# Switch cache implementation
cache.type=memory  # or redis

# Toggle features
features.analytics=true
features.reporting=false

# Set environment
spring.profiles.active=dev  # or prod, test
```

## Common Patterns

1. **Default/Fallback Beans:** `@ConditionalOnMissingBean`
2. **Feature Toggles:** `@ConditionalOnProperty`
3. **Environment-Specific:** `@Profile`
4. **Dependent Beans:** `@ConditionalOnBean`

## All Conditional Annotations

- `@ConditionalOnProperty` - Based on properties
- `@ConditionalOnBean` - If bean exists
- `@ConditionalOnMissingBean` - If bean doesn't exist
- `@ConditionalOnClass` - If class on classpath
- `@ConditionalOnMissingClass` - If class not on classpath
- `@ConditionalOnResource` - If resource exists
- `@ConditionalOnExpression` - SpEL expression
- `@ConditionalOnJava` - Java version
- `@ConditionalOnWebApplication` - Web app
- `@Profile` - Environment profiles
