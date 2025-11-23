# Project 2: Lombok Integration

## Overview

This project demonstrates **Project Lombok** - a Java library that eliminates boilerplate code by auto-generating common methods at compile time using annotations.

## What is Lombok?

Lombok is a compile-time code generator that:
- Reduces boilerplate code by 50-70%
- Generates getters, setters, constructors, equals, hashCode, toString
- Implements design patterns like Builder
- Provides logging utilities
- Makes code more maintainable and readable

## Key Lombok Annotations Demonstrated

### 1. @Data
Generates all the boilerplate for a POJO:
- Getters for all fields
- Setters for all non-final fields
- `toString()`
- `equals()` and `hashCode()`
- `RequiredArgsConstructor` for final fields

```java
// WITHOUT Lombok (80+ lines)
public class Product {
    private Long id;
    private String name;
    // ... manual getters, setters, constructors, equals, hashCode, toString
}

// WITH Lombok (5 lines)
@Data
public class Product {
    private Long id;
    private String name;
}
```

### 2. @RequiredArgsConstructor
Generates a constructor with all `final` fields as parameters.

**Perfect for Dependency Injection in Spring!**

```java
// WITHOUT Lombok
@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
}

// WITH Lombok
@RestController
@RequiredArgsConstructor  // Constructor generated automatically!
public class ProductController {
    private final ProductService productService;
}
```

### 3. @Builder
Implements the Builder design pattern for object creation.

```java
@Builder
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
}

// Usage
Product product = Product.builder()
    .id(1L)
    .name("Laptop")
    .price(new BigDecimal("1299.99"))
    .build();
```

### 4. @Slf4j
Generates a logger field automatically.

```java
// WITHOUT Lombok
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    public void doSomething() {
        log.info("Doing something");
    }
}

// WITH Lombok
@Slf4j
public class ProductService {
    public void doSomething() {
        log.info("Doing something");  // log field generated automatically
    }
}
```

### 5. @Value
Creates an **immutable** class (all fields are `final` and `private`).

```java
@Value
public class ProductDTO {
    Long id;
    String name;
    BigDecimal price;
}

// Generated:
// - final fields
// - All-args constructor
// - Getters (no setters - immutable!)
// - equals, hashCode, toString
```

### 6. @NoArgsConstructor & @AllArgsConstructor
```java
@NoArgsConstructor  // Generates: public Product() {}
@AllArgsConstructor // Generates: public Product(Long id, String name, ...) {}
public class Product {
    private Long id;
    private String name;
}
```

## Project Structure

```
02-lombok-integration/
├── src/main/java/com/example/lombok/
│   ├── LombokIntegrationApplication.java    # Main application
│   ├── controller/
│   │   └── ProductController.java           # REST controller (@RequiredArgsConstructor, @Slf4j)
│   ├── service/
│   │   ├── ProductService.java              # Service WITH Lombok
│   │   └── ProductServiceWithoutLombok.java # Service WITHOUT Lombok (comparison)
│   └── model/
│       ├── Product.java                     # Entity WITH Lombok (@Data, @Builder)
│       └── ProductWithoutLombok.java        # Entity WITHOUT Lombok (verbose)
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

The application will start on `http://localhost:8082`

### 2. Test the Endpoints

**Get all products:**
```bash
curl http://localhost:8082/api/products
```

**Get product by ID:**
```bash
curl http://localhost:8082/api/products/1
```

**Create a product:**
```bash
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Keyboard",
    "description": "Mechanical keyboard",
    "price": 89.99
  }'
```

**Update a product:**
```bash
curl -X PUT http://localhost:8082/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Laptop",
    "description": "Updated gaming laptop",
    "price": 1499.99
  }'
```

**Delete a product:**
```bash
curl -X DELETE http://localhost:8082/api/products/2
```

## Code Comparison

### Without Lombok (ProductWithoutLombok.java)
- **80+ lines** for a simple POJO
- Manual getters, setters, constructors
- Manual equals, hashCode, toString
- Error-prone when adding new fields

### With Lombok (Product.java)
- **~25 lines** for the same functionality
- All boilerplate generated at compile-time
- Adding new fields is trivial
- Less code to maintain

**Lombok reduces code by 70%!**

## Common Lombok Annotations Quick Reference

| Annotation | Description | Use Case |
|------------|-------------|----------|
| `@Data` | Generates getters, setters, toString, equals, hashCode | DTOs, Entities |
| `@Value` | Creates immutable class | Immutable DTOs, Value Objects |
| `@Builder` | Implements Builder pattern | Complex object creation |
| `@Slf4j` | Generates SLF4J logger | Logging |
| `@RequiredArgsConstructor` | Constructor for final fields | Dependency Injection |
| `@NoArgsConstructor` | No-argument constructor | JPA Entities |
| `@AllArgsConstructor` | All-fields constructor | Testing, Builders |
| `@Getter` | Generates getters only | When you don't want setters |
| `@Setter` | Generates setters only | Rarely used alone |
| `@ToString` | Generates toString() | Debugging |
| `@EqualsAndHashCode` | Generates equals & hashCode | Collections, comparisons |

## IDE Setup

### IntelliJ IDEA
1. Install Lombok plugin: `Settings > Plugins > Search "Lombok" > Install`
2. Enable annotation processing: `Settings > Build > Compiler > Annotation Processors > Enable`

### Eclipse
1. Download lombok.jar from https://projectlombok.org/download
2. Run: `java -jar lombok.jar`
3. Select Eclipse installation directory
4. Click "Install/Update"

### VS Code
1. Install "Lombok Annotations Support" extension
2. Restart VS Code

## Benefits of Using Lombok

1. **Less Boilerplate** - 50-70% reduction in code
2. **Maintainability** - Less code to maintain and test
3. **Readability** - Focus on business logic, not getters/setters
4. **Safety** - Generated code is tested and bug-free
5. **Productivity** - Write features faster
6. **Standard Patterns** - Builder, logging, etc., implemented correctly

## Potential Drawbacks

1. **IDE Support Required** - Need Lombok plugin
2. **Compile-Time Magic** - Some developers prefer explicit code
3. **Debugging** - Generated code not visible in source
4. **Learning Curve** - Team needs to understand Lombok annotations

## Best Practices

1. **Use @RequiredArgsConstructor for DI** - Perfect for Spring dependency injection
2. **Use @Slf4j for logging** - Industry standard SLF4J
3. **Use @Builder for complex objects** - Better than telescoping constructors
4. **Use @Value for immutable DTOs** - Thread-safe and clean
5. **Combine annotations wisely** - `@Data` + `@Builder` + `@NoArgsConstructor` + `@AllArgsConstructor`

## When to Use Lombok

**Use Lombok when:**
- Creating POJOs, DTOs, Entities
- Implementing dependency injection (Spring)
- Building complex objects
- Adding logging to classes

**Avoid Lombok when:**
- Team is unfamiliar with it
- Custom logic needed in getters/setters
- Working with frameworks that require specific constructors

## Learning Resources

- **Official Site**: https://projectlombok.org/
- **Feature Overview**: https://projectlombok.org/features/
- **Lombok in Spring Boot**: Search "Lombok Spring Boot tutorial"

## Next Steps

After understanding Lombok, explore:
1. **Java Records** (Project 3) - Native Java alternative for DTOs
2. **MapStruct** - Use with Lombok for DTO mapping
3. **Validation** - Combine Lombok with Bean Validation

---

**Key Takeaway**: Lombok eliminates boilerplate code, making Java development more productive and enjoyable. In modern Spring Boot applications, using Lombok is a best practice, especially for dependency injection and logging.
