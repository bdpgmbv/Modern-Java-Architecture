# Java Records Interview Guide

## Common Interview Questions & Answers

### 1. What are Java Records and why were they introduced?

**Answer:**
"Java Records were introduced in Java 14 as a preview feature and finalized in Java 16. They are a special kind of class designed to be **immutable data carriers** - perfect for DTOs, value objects, and data transfer.

**Problems Records solve:**
1. **Boilerplate code** - Traditional DTOs require 50-70+ lines for getters, setters, equals, hashCode, toString
2. **Error-prone** - Manual equals/hashCode implementations can have bugs
3. **Maintenance burden** - Adding new fields requires updating multiple methods

**Benefits:**
- Reduce code by 90%
- Immutable by default (thread-safe)
- Built into Java (no Lombok needed)
- Compiler-generated methods are bug-free
- Clear intent - 'this is just data'

**Example:**
```java
// Traditional DTO: 70+ lines
public class UserDTO { /* getters, setters, equals, hashCode, toString... */ }

// Record: 1 line!
public record UserDTO(Long id, String username, String email) {}
```"

---

### 2. What does the Java compiler automatically generate for Records?

**Answer:**
"When you declare a Record, the compiler automatically generates:

1. **Private final fields** - for each component
2. **Canonical constructor** - with all components as parameters
3. **Accessor methods** - NO 'get' prefix! Just `user.username()` not `user.getUsername()`
4. **equals() method** - compares all fields
5. **hashCode() method** - uses all fields
6. **toString() method** - includes all fields

**Example:**
```java
public record UserDTO(Long id, String username) {}

// Compiler generates:
// - private final Long id;
// - private final String username;
// - public UserDTO(Long id, String username) { ... }
// - public Long id() { return id; }
// - public String username() { return username; }
// - public boolean equals(Object o) { ... }
// - public int hashCode() { ... }
// - public String toString() { ... }
```

**Interview Tip:** Emphasize that accessor methods have NO 'get' prefix - this is a common mistake!"

---

### 3. Explain the compact constructor in Records. When would you use it?

**Answer:**
"The compact constructor is a special constructor syntax unique to Records. It runs **BEFORE** field assignment and is perfect for validation and normalization.

**Key characteristics:**
- NO parameter list (implicit access to all components)
- Runs BEFORE fields are assigned
- Can modify parameter values
- Can throw exceptions for validation
- Fields are automatically assigned after the block

**Example:**
```java
public record Email(String value) {
    // Compact constructor - no parameter list!
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }

        // Normalize
        value = value.trim().toLowerCase();

        // Validation
        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Field automatically assigned after this block
    }
}
```

**Use cases:**
1. Input validation
2. Data normalization (trim, lowercase, etc.)
3. Defensive copies
4. Default values
5. Invariant enforcement

**Interview Tip:** Show you understand the difference between compact constructors and regular constructors!"

---

### 4. How do Records compare to Lombok's @Value or @Data?

**Answer:**
| Feature | Record | Lombok @Value | Lombok @Data |
|---------|--------|---------------|--------------|
| **Java Version** | Java 16+ | Any | Any |
| **Dependency** | None (built-in) | Lombok | Lombok |
| **Mutability** | Immutable | Immutable | Mutable |
| **IDE Support** | Always works | Plugin required | Plugin required |
| **Customization** | Medium | Limited | High |
| **Best For** | DTOs, Value Objects | Immutable DTOs | Entities, Services |

**When to use each:**
- **Records**: Modern Java projects (Java 16+), DTOs, immutable data
- **Lombok @Value**: Need Lombok anyway, older Java versions
- **Lombok @Data**: Mutable objects, entities

**Example:**
```java
// Lombok @Value
@Value
public class UserDTO {
    Long id;
    String username;
}

// Record (preferred in modern Java)
public record UserDTO(Long id, String username) {}
```

**Interview Tip:** Show awareness that Records are the modern, recommended approach for DTOs in Java 16+."

---

### 5. Can you modify a Record after creation? How do you update Records?

**Answer:**
"No, Records are **immutable** - all fields are automatically `final`. You cannot modify a Record after creation.

**To 'update' a Record, create a new instance:**

```java
public record UserDTO(Long id, String username, String email) {}

UserDTO user = new UserDTO(1L, "john", "john@example.com");

// WRONG - cannot modify (compilation error)
user.username = "jane";  // ERROR: fields are final

// CORRECT - create new instance
UserDTO updated = new UserDTO(
    user.id(),           // keep same
    "jane",              // change username
    user.email()         // keep same
);
```

**Alternative - 'with' methods:**
```java
public record UserDTO(Long id, String username, String email) {

    public UserDTO withUsername(String newUsername) {
        return new UserDTO(this.id, newUsername, this.email);
    }

    public UserDTO withEmail(String newEmail) {
        return new UserDTO(this.id, this.username, newEmail);
    }
}

// Usage
UserDTO updated = user.withUsername("jane");
```

**Interview Tip:** Explain that immutability is a feature, not a limitation - it makes Records thread-safe!"

---

### 6. When should you NOT use Records?

**Answer:**
"Records are NOT suitable for:

**1. JPA/Hibernate Entities**
```java
// WRONG - JPA needs setters and no-arg constructor
@Entity
public record User(Long id, String name) {}

// CORRECT - Use regular class
@Entity
@Data
public class User {
    @Id private Long id;
    private String name;
}
```

**2. Mutable Objects**
Records are immutable - if you need setters, use a regular class.

**3. Inheritance**
Records are implicitly `final` and cannot extend other classes (except Object).
```java
// WRONG
public record AdminUser(...) extends User {} // ERROR!
```

**4. Lazy Initialization**
All Record fields must be initialized in the constructor.

**5. Objects Requiring Custom Field Behavior**
If you need custom getter logic, use a regular class.

**Interview Tip:** Show you understand when Records are appropriate vs when to use traditional classes."

---

### 7. How do Records work with Spring Boot and Jackson?

**Answer:**
"Records work seamlessly with Spring Boot and Jackson:

**1. Request Deserialization (@RequestBody):**
```java
public record CreateUserRequest(String username, String email) {}

@PostMapping
public ResponseEntity<UserDTO> create(@RequestBody CreateUserRequest request) {
    // Jackson automatically calls: new CreateUserRequest(username, email)
}
```

**2. Response Serialization:**
```java
public record UserDTO(Long id, String username) {}

@GetMapping("/{id}")
public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
    UserDTO user = new UserDTO(1L, "john");
    return ResponseEntity.ok(user);
    // Jackson serializes to: {"id":1,"username":"john"}
}
```

**3. Validation:**
```java
public record CreateUserRequest(
    @NotBlank @Size(min = 3) String username,
    @Email String email
) {}

@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody CreateUserRequest request) {
    // Spring validates automatically!
}
```

**4. Jackson Annotations Work:**
```java
public record UserDTO(
    Long id,
    @JsonProperty("user_name") String username,
    @JsonIgnore String password
) {}
```

**Interview Tip:** Demonstrate that Records are first-class citizens in Spring Boot!"

---

### 8. What's the difference between accessors in Records vs regular classes?

**Answer:**
"Record accessor methods do NOT have the 'get' prefix:

**Regular Class:**
```java
public class UserDTO {
    private String username;

    public String getUsername() {  // 'get' prefix
        return username;
    }
}

// Usage
user.getUsername()  // 'get' prefix
```

**Record:**
```java
public record UserDTO(String username) {}

// Generated accessor
public String username() {  // NO 'get' prefix
    return username;
}

// Usage
user.username()  // NO 'get' prefix
```

**Why this matters:**
1. **Jackson** - Works with both styles automatically
2. **Code** - Must remember no 'get' prefix
3. **Consistency** - All Records follow same pattern

**Interview Tip:** This is a common mistake in interviews - showing you know this demonstrates hands-on experience!"

---

### 9. Can you add custom methods to Records?

**Answer:**
"Yes! Records can have:
1. Custom instance methods
2. Static methods
3. Static factory methods
4. Overridden methods

**Example:**
```java
public record UserDTO(Long id, String username, String role) {

    // Static factory method
    public static UserDTO admin(Long id, String username) {
        return new UserDTO(id, username, "ADMIN");
    }

    // Instance method
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    // Static utility method
    public static boolean isValidRole(String role) {
        return role.matches("USER|ADMIN|MODERATOR");
    }

    // Override generated toString
    @Override
    public String toString() {
        return String.format("User[%d: %s]", id, username);
    }
}

// Usage
UserDTO admin = UserDTO.admin(1L, "john");
if (admin.isAdmin()) {
    System.out.println(admin);  // Custom toString
}
```

**Best Practice:** Keep Records simple. If you need complex logic, consider using a regular class or service."

---

### 10. How do you handle validation in Records?

**Answer:**
"Records support two types of validation:

**1. Bean Validation Annotations:**
```java
public record CreateUserRequest(
    @NotBlank(message = "Username required")
    @Size(min = 3, max = 50)
    String username,

    @Email(message = "Invalid email")
    String email,

    @Size(min = 8)
    String password
) {}

// In Controller
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody CreateUserRequest request) {
    // Validation happens automatically!
}
```

**2. Compact Constructor Validation:**
```java
public record Email(String value) {
    public Email {
        // Validation in compact constructor
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }

        value = value.trim().toLowerCase();

        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
```

**3. Combining Both:**
```java
public record CreateUserRequest(
    @NotBlank String username,  // Bean Validation
    @Email String email
) {
    public CreateUserRequest {
        // Additional validation in compact constructor
        username = username.trim().toLowerCase();

        if (username.contains(" ")) {
            throw new IllegalArgumentException("Username cannot contain spaces");
        }
    }
}
```

**Interview Tip:** Show that you can use both validation strategies together for comprehensive validation!"

---

### 11. Can Records implement interfaces?

**Answer:**
"Yes! Records can implement interfaces, but they CANNOT extend classes.

**Example:**
```java
// Interface
public interface Identifiable {
    Long getId();
}

// Record implementing interface
public record UserDTO(Long id, String username) implements Identifiable {
    // The getId() method requirement is satisfied by the id() accessor
    // But we need to match the method name:

    @Override
    public Long getId() {
        return id();  // Delegates to generated accessor
    }
}
```

**Sealed interfaces with Records:**
```java
public sealed interface Shape permits Circle, Rectangle {}

public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
```

**What Records CANNOT do:**
```java
// WRONG - Records cannot extend classes
public record UserDTO(...) extends BaseDTO {}  // ERROR!

// CORRECT - Records can only extend java.lang.Record (implicit)
```

**Interview Tip:** Mention sealed interfaces if discussing advanced Record usage - shows deep knowledge!"

---

### 12. How do you create a Record from another Record (transformations)?

**Answer:**
"There are several approaches:

**1. Constructor:**
```java
public record UserDTO(Long id, String username, String email) {}
public record UserSummary(Long id, String username) {}

// Transform
UserDTO full = new UserDTO(1L, "john", "john@example.com");
UserSummary summary = new UserSummary(full.id(), full.username());
```

**2. Static Factory Method:**
```java
public record UserSummary(Long id, String username) {
    public static UserSummary from(UserDTO dto) {
        return new UserSummary(dto.id(), dto.username());
    }
}

// Usage
UserSummary summary = UserSummary.from(full);
```

**3. Mapper Class:**
```java
public class UserMapper {
    public static UserSummary toSummary(UserDTO dto) {
        return new UserSummary(dto.id(), dto.username());
    }
}
```

**4. MapStruct (for complex mappings):**
```java
@Mapper
public interface UserMapper {
    UserSummary toSummary(UserDTO dto);
}
```

**Interview Tip:** Show awareness of different transformation patterns and when to use each!"

---

## How to Present Records Knowledge in Interviews

### 1. When Discussing Modern Java
"In my recent projects using Java 17, I've adopted Java Records for all DTOs. Records reduce boilerplate by 90% and provide immutability by default, which improves code quality and thread-safety. For example, I replaced our traditional DTOs with Records and removed over 3,000 lines of boilerplate code."

### 2. When Discussing API Design
"For REST API request/response objects, I use Java Records. They're perfect because DTOs should be immutable, and Records provide that by default. The compact constructor feature also allows me to validate and normalize input right at object creation, ensuring data integrity."

### 3. When Discussing Code Quality
"I prefer Records over Lombok for DTOs in modern Java projects because they're built into the language, require no external dependencies, and have guaranteed IDE support. The immutability-by-default also prevents common bugs related to shared mutable state."

### 4. When Comparing with Lombok
"While Lombok is great for reducing boilerplate in entities and services, for DTOs specifically, I prefer Java Records in Java 16+ projects. Records are built into Java, have no dependency overhead, and clearly signal 'this is just immutable data.'"

---

## Code Demonstration for Interviews

### Quick Comparison Example

**Before (Traditional DTO - 70+ lines):**
```java
public class UserDTO {
    private final Long id;
    private final String username;
    private final String email;

    public UserDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) { /* 15 lines */ }

    @Override
    public int hashCode() { /* 5 lines */ }

    @Override
    public String toString() { /* 10 lines */ }
}
```

**After (Record - 3 lines):**
```java
public record UserDTO(Long id, String username, String email) {}
```

**Same functionality, 90% less code!**

---

## Advanced Topics to Mention

### 1. Pattern Matching with Records (Java 16+)
```java
if (obj instanceof UserDTO(var id, var username, var email)) {
    System.out.println("User: " + username);
}
```

### 2. Sealed Classes with Records
```java
public sealed interface Result<T> permits Success, Failure {}
public record Success<T>(T value) implements Result<T> {}
public record Failure<T>(String error) implements Result<T> {}
```

### 3. Local Records (Java 16+)
```java
public void processUsers(List<User> users) {
    record UserStats(String username, int count) {}

    var stats = users.stream()
        .map(u -> new UserStats(u.getUsername(), u.getCount()))
        .toList();
}
```

---

## Common Interview Mistakes to Avoid

### 1. Using 'get' prefix with Records
```java
// WRONG
user.getUsername()

// CORRECT
user.username()
```

### 2. Trying to use Records for JPA Entities
```java
// WRONG
@Entity
public record User(Long id, String name) {}

// CORRECT - Use regular class for entities
```

### 3. Forgetting Records are Immutable
```java
// WRONG
user.setUsername("jane")  // No setters exist!

// CORRECT
UserDTO updated = new UserDTO(user.id(), "jane", user.email());
```

---

## Behavioral Interview Tips

### When Asked: "What modern Java features have you adopted?"
"I've fully adopted Java Records for DTOs in our Java 17 migration. This eliminated thousands of lines of boilerplate code and improved code quality. The immutability-by-default also caught several potential concurrency bugs during code review."

### When Asked: "How do you ensure code quality?"
"For DTOs, I use Java Records which are immutable by default. This eliminates entire categories of bugs related to shared mutable state. The compact constructor also allows validation at object creation, ensuring we never have invalid DTOs in our system."

---

## Key Points to Remember

1. **Records = Immutable data carriers** (perfect for DTOs)
2. **90% less code** than traditional DTOs
3. **No 'get' prefix** in accessors: `user.username()` not `user.getUsername()`
4. **Compact constructor** for validation/normalization
5. **Built into Java 16+** (no external dependencies)
6. **Cannot be used for JPA entities** (need mutability)
7. **Work seamlessly with Spring Boot** and Jackson

---

## Final Interview Strategy

**DO:**
- Show Records in REST API examples
- Demonstrate compact constructor for validation
- Compare Records with Lombok objectively
- Explain when NOT to use Records

**DON'T:**
- Claim Records replace all classes
- Forget the 'no get prefix' rule
- Try to use Records for entities
- Ignore Lombok (it has its place)

---

**Remember:** Java Records represent modern Java best practices. Demonstrating knowledge of Records shows you're up-to-date with modern Java features and understand immutability, thread-safety, and clean code principles!
