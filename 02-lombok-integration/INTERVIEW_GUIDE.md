# Lombok Interview Guide

## Common Interview Questions & Answers

### 1. What is Lombok and why should we use it?

**Answer:**
"Lombok is a Java library that reduces boilerplate code by automatically generating common methods like getters, setters, constructors, equals, hashCode, and toString at compile time using annotations.

**Benefits:**
- Reduces code by 50-70%
- Improves readability - focus on business logic
- Reduces maintenance burden
- Eliminates human errors in boilerplate code
- Increases developer productivity

**Example:** A simple POJO that would take 80 lines without Lombok can be written in just 10 lines with `@Data` annotation."

---

### 2. How does Lombok work internally?

**Answer:**
"Lombok works at **compile time** using Java's **Annotation Processing API**. Here's the process:

1. During compilation, Lombok's annotation processor intercepts Java source files
2. It analyzes Lombok annotations (@Data, @Getter, etc.)
3. It modifies the Abstract Syntax Tree (AST) to inject the generated code
4. The Java compiler then compiles the enhanced bytecode

**Important:** The generated code is NOT in your source files - it's in the compiled `.class` files. IDEs need Lombok plugins to understand the generated methods for autocomplete and error checking."

---

### 3. Explain @Data annotation. What does it generate?

**Answer:**
"`@Data` is a composite annotation that generates all the boilerplate for a standard POJO:

**Generates:**
- `@Getter` for all fields
- `@Setter` for all non-final fields
- `@ToString` (includes all fields)
- `@EqualsAndHashCode` (uses all fields)
- `@RequiredArgsConstructor` (for final fields only)

**Example:**
```java
@Data
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
}
```

This is equivalent to 60-80 lines of manual code!"

---

### 4. What's the difference between @Data and @Value?

**Answer:**
| Feature | @Data | @Value |
|---------|-------|--------|
| Mutability | Mutable | **Immutable** |
| Fields | Private | Private **final** |
| Setters | Yes | No |
| Constructor | Required args | All args |
| Use Case | Entities, DTOs | Immutable DTOs, Value Objects |

**Example:**
```java
// Mutable
@Data
public class Product {
    private String name; // can be changed
}

// Immutable
@Value
public class ProductDTO {
    String name; // final - cannot be changed
}
```

**Interview Tip:** Mention that `@Value` is thread-safe because it's immutable!"

---

### 5. Explain @RequiredArgsConstructor and its use in Spring Boot

**Answer:**
"`@RequiredArgsConstructor` generates a constructor with parameters for all `final` fields.

**In Spring Boot, this is the RECOMMENDED way for dependency injection:**

```java
@RestController
@RequiredArgsConstructor  // Constructor injection
public class ProductController {
    private final ProductService service; // final = required
}
```

**Generated code:**
```java
public ProductController(ProductService service) {
    this.service = service;
}
```

**Why this is better than @Autowired:**
- Constructor injection is Spring's recommended approach
- Makes dependencies explicit and required
- Better for testing (can pass mocks easily)
- Immutable dependencies (final fields)
- No need for @Autowired annotation

**Interview Tip:** Say 'In modern Spring Boot, we avoid @Autowired on fields and use constructor injection with @RequiredArgsConstructor instead.'"

---

### 6. What is @Builder and when should you use it?

**Answer:**
"`@Builder` implements the Builder design pattern for fluent object creation.

**Use when:**
- Creating objects with many fields
- Optional parameters
- Better readability than telescoping constructors

**Example:**
```java
@Builder
@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
}

// Usage
Product product = Product.builder()
    .id(1L)
    .name("Laptop")
    .price(new BigDecimal("1299.99"))
    .build();
```

**Benefits:**
- Readable, chainable API
- Can set fields in any order
- Can skip optional fields
- Immutable after build()

**Interview Tip:** Compare with telescoping constructors to show why Builder is better!"

---

### 7. Explain @Slf4j. Why use it over manual logger creation?

**Answer:**
"`@Slf4j` automatically generates a logger field:

```java
// Manual way
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
}

// Lombok way
@Slf4j
public class ProductService {
    // log field generated automatically!
}
```

**Benefits:**
- Less boilerplate
- Uses SLF4J (industry standard)
- Consistent naming (always 'log')
- Automatic class name in logger

**Interview Tip:** Mention that SLF4J is a facade that works with any logging framework (Logback, Log4j2, etc.)."

---

### 8. What are the potential drawbacks of using Lombok?

**Answer:**
"Be honest about limitations - shows maturity:

**Drawbacks:**
1. **IDE Support Required** - Need Lombok plugin installed
2. **Build Tool Config** - Must add Lombok dependency
3. **Learning Curve** - Team must understand annotations
4. **Debugging** - Generated code not visible in source
5. **Magic Code** - Some prefer explicit code
6. **Compatibility** - Rarely, conflicts with other annotation processors

**How to address:**
- Ensure entire team uses IDEs with Lombok plugin
- Document Lombok usage in project README
- Use Lombok for simple cases (getters, setters, logging)
- For complex logic, write explicit code

**Interview Tip:** Acknowledge drawbacks but explain how they're manageable in practice."

---

### 9. Can you combine multiple Lombok annotations? Give an example.

**Answer:**
"Yes! Lombok annotations can be combined:

```java
@Data                  // Getters, setters, toString, equals, hashCode
@Builder               // Builder pattern
@NoArgsConstructor     // Default constructor (for JPA)
@AllArgsConstructor    // All-args constructor (for Builder)
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
}
```

**Why combine them:**
- `@Data` + `@Builder` = POJO with builder
- `@NoArgsConstructor` needed for JPA/Hibernate
- `@AllArgsConstructor` needed for `@Builder` to work

**Common combination for JPA entities:**
```java
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private Long id;
}
```"

---

### 10. How do you handle Lombok in unit tests?

**Answer:**
"Lombok works seamlessly in tests:

```java
@Test
void testProductBuilder() {
    // Using @Builder
    Product product = Product.builder()
        .name("Test Product")
        .price(new BigDecimal("99.99"))
        .build();

    // Using generated getters
    assertEquals("Test Product", product.getName());
}

@Test
void testConstructorInjection() {
    // @RequiredArgsConstructor allows easy mocking
    ProductService mockService = mock(ProductService.class);
    ProductController controller = new ProductController(mockService);

    // Test with mock
}
```

**Benefits for testing:**
- Builder pattern simplifies test data creation
- Constructor injection (via @RequiredArgsConstructor) makes mocking easy
- equals() and hashCode() work correctly in assertions

**Interview Tip:** Mention that constructor injection (enabled by Lombok) makes testing easier than field injection."

---

### 11. What's the difference between @Getter/@Setter and @Data?

**Answer:**
| Annotation | Generates | Use Case |
|------------|-----------|----------|
| `@Getter` | Only getters | Read-only properties |
| `@Setter` | Only setters | Rarely used alone |
| `@Getter` + `@Setter` | Getters and setters | Custom control |
| `@Data` | Getters, setters, toString, equals, hashCode | Complete POJO |

**Example:**
```java
// Read-only class
@Getter
@RequiredArgsConstructor
public class ImmutableProduct {
    private final String name;
    private final BigDecimal price;
    // No setters generated!
}

// Full POJO
@Data
public class Product {
    private String name;
    // Getters + Setters + toString + equals + hashCode
}
```"

---

### 12. Is Lombok production-ready? Is it widely used?

**Answer:**
"**Yes, absolutely!** Lombok is production-ready and widely adopted:

**Evidence:**
- Used by thousands of companies worldwide
- Stable since 2009 (15+ years)
- Over 10 million downloads per month
- Integrated with all major IDEs
- Recommended in Spring Boot best practices
- Works with Java 8 through Java 21+

**Companies using Lombok:**
- Many Fortune 500 companies
- Startups to enterprises
- Open-source projects

**Interview Tip:** If asked about stability, mention its maturity and widespread adoption in the Java ecosystem."

---

## How to Present Lombok Knowledge in Interviews

### 1. When Discussing Spring Boot Projects
"In my Spring Boot projects, I use Lombok extensively to reduce boilerplate. For example, I use `@RequiredArgsConstructor` for constructor-based dependency injection, which is Spring's recommended approach. This eliminates the need for manual constructors and `@Autowired` annotations."

### 2. When Discussing Code Quality
"To improve code maintainability, I use Lombok's `@Data` for DTOs and entities. This reduces code by 70% and eliminates human errors in equals/hashCode implementations. It also makes adding new fields trivial."

### 3. When Discussing Design Patterns
"For complex object creation, I use Lombok's `@Builder` annotation, which implements the Builder pattern. This provides a fluent, readable API for object construction, especially when dealing with objects with many optional fields."

### 4. When Discussing Logging
"I use `@Slf4j` for logging across all my services. This eliminates the boilerplate of manually creating logger instances and ensures consistent logging practices using SLF4J, which is the industry standard facade."

---

## Code Demonstration for Interviews

### Quick Before/After Example

**Before Lombok (80+ lines):**
```java
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;

    public Product() {}

    public Product(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public boolean equals(Object o) { /* 15 lines */ }

    @Override
    public int hashCode() { /* 5 lines */ }

    @Override
    public String toString() { /* 10 lines */ }
}
```

**After Lombok (10 lines):**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
}
```

**Same functionality, 90% less code!**

---

## Common Mistakes to Avoid

### 1. Using @Data with JPA Entities
**Wrong:**
```java
@Entity
@Data  // Problem: generates setters for @Id field
public class Product {
    @Id
    private Long id;
}
```

**Better:**
```java
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    private Long id;
}
```

### 2. Forgetting IDE Plugin
"If IDE shows errors but code compiles, install Lombok plugin."

### 3. Using @Data for Entities with Relationships
"For JPA entities with @OneToMany, use `@ToString(exclude = "...")` to avoid circular references."

---

## Technical Deep Dive Questions

### Q: How does Lombok avoid runtime dependencies?
**A:** "Lombok is a **compile-time** only dependency. The generated code is pure Java bytecode with no Lombok references. You only need Lombok during compilation, not at runtime. This is why it's specified as `compileOnly` in Gradle."

### Q: Can Lombok work with Java Records?
**A:** "Java 14+ Records provide similar functionality to Lombok for immutable data classes. Records are now preferred for simple DTOs in modern Java. However, Lombok still offers more flexibility (mutability, builders, etc.) and works with older Java versions."

### Q: What annotation processor does Lombok use?
**A:** "Lombok uses the Java Annotation Processing API (JSR 269) to process annotations at compile time. It modifies the Abstract Syntax Tree (AST) to inject generated code before final bytecode compilation."

---

## Behavioral Interview Tips

### When Asked: "What tools/libraries have improved your productivity?"
"Lombok has significantly improved my productivity by eliminating boilerplate code. For example, in a recent project with 50+ DTOs, Lombok reduced our codebase by approximately 3,000 lines while maintaining the same functionality. This made our code more maintainable and reduced the time spent on code reviews."

### When Asked: "How do you keep up with Java ecosystem?"
"I actively adopt modern Java practices like using Lombok for boilerplate reduction, Java Records for DTOs, and constructor-based dependency injection in Spring Boot. These practices align with industry standards and improve code quality."

---

## Key Points to Remember

1. **Lombok = Compile-time code generation** (not reflection)
2. **@RequiredArgsConstructor = Best practice for Spring DI**
3. **@Slf4j = Industry standard logging**
4. **@Data = Complete POJO in one annotation**
5. **@Builder = Clean object creation**
6. **@Value = Immutable classes**

---

## Final Interview Strategy

**DO:**
- Mention Lombok when discussing Spring Boot projects
- Show before/after code examples
- Acknowledge both benefits and limitations
- Emphasize productivity and maintainability gains

**DON'T:**
- Claim Lombok is perfect for everything
- Forget to mention IDE setup requirements
- Ignore alternatives like Java Records
- Overuse jargon without explaining

---

**Remember:** Lombok is not just a tool - it's a demonstration of your awareness of modern Java best practices and your commitment to writing clean, maintainable code!
