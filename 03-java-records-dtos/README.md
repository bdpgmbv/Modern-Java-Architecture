# Project 3: Java Records for DTOs

## Overview

This project demonstrates **Java Records** (Java 14+, finalized in Java 16) - a modern, concise way to create immutable data carriers, perfect for DTOs (Data Transfer Objects).

## What are Java Records?

Java Records are a special kind of class designed for **immutable data carriers**. They dramatically reduce boilerplate code while providing:
- Immutability by default (all fields are final)
- Auto-generated constructor, accessors, equals, hashCode, toString
- Built into Java (no external dependencies like Lombok)
- Type-safe and null-safe (with compact constructors)

## The Problem: Traditional DTOs are Verbose

**Traditional DTO (70+ lines):**
```java
public class TraditionalUserDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String role;

    // Constructor (5 lines)
    public TraditionalUserDTO(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // 4 getters (16 lines)
    public Long getId() { return id; }
    public String getUsername() { return username; }
    // ... etc

    // equals() (15 lines)
    // hashCode() (5 lines)
    // toString() (10 lines)
}
```

**Modern Record (5 lines):**
```java
public record UserDTO(
    Long id,
    String username,
    String email,
    String role
) {}
```

**Same functionality, 90% less code!**

## Key Features Demonstrated

### 1. Basic Record Syntax

```java
// Simple Record - that's all you need!
public record UserDTO(Long id, String username, String email, String role) {}

// Usage
UserDTO user = new UserDTO(1L, "john", "john@example.com", "USER");

// Accessing fields (NO 'get' prefix!)
String username = user.username();  // NOT user.getUsername()
String email = user.email();
```

**What's Generated Automatically:**
- Private final fields
- Public constructor with all fields
- Public accessor methods (no 'get' prefix!)
- equals() method
- hashCode() method
- toString() method

### 2. Compact Constructor (Validation & Normalization)

One of the most powerful features of Records!

```java
public record CreateUserRequest(
    String username,
    String email,
    String password,
    String role
) {
    /**
     * Compact Constructor - runs BEFORE field assignment
     * Perfect for validation and normalization
     */
    public CreateUserRequest {
        // Normalize
        username = username.trim().toLowerCase();
        email = email.trim().toLowerCase();
        role = role.trim().toUpperCase();

        // Validate
        if (username.contains(" ")) {
            throw new IllegalArgumentException("Username cannot contain spaces");
        }

        if (!role.matches("USER|ADMIN|MODERATOR")) {
            throw new IllegalArgumentException("Invalid role");
        }

        // Fields are automatically assigned AFTER this block
    }
}
```

**Key Points:**
- NO parameter list in compact constructor
- Runs BEFORE field assignment
- Can modify parameter values
- Can throw exceptions for validation
- Fields assigned automatically after the block

### 3. Bean Validation with Records

Records work seamlessly with Spring's validation framework:

```java
public record CreateUserRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    String username,

    @NotBlank
    @Email(message = "Email must be valid")
    String email,

    @Size(min = 8)
    String password,

    @NotBlank
    String role
) {
    // Compact constructor for additional validation
    public CreateUserRequest {
        username = username.trim().toLowerCase();
        // ...
    }
}

// In Controller
@PostMapping
public ResponseEntity<UserDTO> create(@Valid @RequestBody CreateUserRequest request) {
    // Both Bean Validation AND compact constructor run automatically!
}
```

### 4. Custom Methods in Records

You can add custom methods to Records:

```java
public record UserResponseDTO(Long id, String username, String email, String role) {

    // Static factory method
    public static UserResponseDTO from(UserDTO dto) {
        return new UserResponseDTO(dto.id(), dto.username(), dto.email(), dto.role());
    }

    // Instance methods
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public String getDisplayName() {
        return username + " (" + role + ")";
    }

    // Override generated methods if needed
    @Override
    public String toString() {
        return String.format("User[id=%d, username=%s]", id, username);
    }
}
```

### 5. Multiple Constructors

You can have additional constructors that delegate to the canonical constructor:

```java
public record UserResponseDTO(
    Long id,
    String username,
    String email,
    String role,
    LocalDateTime createdAt
) {
    // Compact constructor
    public UserResponseDTO {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Alternative constructor with fewer parameters
    public UserResponseDTO(Long id, String username, String email, String role) {
        this(id, username, email, role, LocalDateTime.now());
    }
}
```

## Project Structure

```
03-java-records-dtos/
├── src/main/java/com/example/records/
│   ├── JavaRecordsDtosApplication.java  # Main application
│   ├── controller/
│   │   └── UserController.java          # REST endpoints
│   ├── service/
│   │   └── UserService.java             # Business logic
│   ├── model/
│   │   └── User.java                    # Entity (NOT a Record - mutable)
│   └── dto/
│       ├── TraditionalUserDTO.java      # Traditional DTO (comparison)
│       ├── UserDTO.java                 # Simple Record DTO
│       ├── CreateUserRequest.java       # Record with validation
│       └── UserResponseDTO.java         # Record with custom methods
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

Application starts on `http://localhost:8083`

### 2. Test the Endpoints

**Get all users:**
```bash
curl http://localhost:8083/api/users
```

**Get user by ID:**
```bash
curl http://localhost:8083/api/users/1
```

**Search users by role:**
```bash
curl "http://localhost:8083/api/users/search?role=ADMIN"
```

**Create a user (with validation):**
```bash
curl -X POST http://localhost:8083/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

**Test validation - invalid email:**
```bash
curl -X POST http://localhost:8083/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob",
    "email": "invalid-email",
    "password": "pass123",
    "role": "USER"
  }'
```

**Test compact constructor validation - username with spaces:**
```bash
curl -X POST http://localhost:8083/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob smith",
    "email": "bob@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

**Update user:**
```bash
curl -X PUT http://localhost:8083/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "updated_admin",
    "email": "admin@updated.com",
    "password": "newpassword123",
    "role": "ADMIN"
  }'
```

**Delete user:**
```bash
curl -X DELETE http://localhost:8083/api/users/2
```

## Records vs Lombok vs Traditional

| Feature | Traditional | Lombok | Records |
|---------|------------|---------|---------|
| **Code Lines** | 70+ | 10-15 | 5 |
| **Mutability** | Manual | Configurable | Immutable |
| **Dependencies** | None | Lombok library | None (built-in Java) |
| **IDE Support** | Always | Plugin required | Always |
| **Customization** | Full | Limited | Medium |
| **Best For** | Legacy code | Entities, Services | DTOs, Value Objects |

## When to Use Records

**Use Records for:**
- DTOs (Data Transfer Objects)
- API request/response objects
- Value Objects
- Immutable configuration
- Event objects
- Query results

**Don't Use Records for:**
- JPA/Hibernate entities (need setters, no-arg constructor)
- Mutable objects
- Objects requiring inheritance (Records are final)
- Lazy initialization scenarios

## Records Best Practices

### 1. Use for DTOs
```java
// Perfect for DTOs
public record UserDTO(Long id, String username, String email) {}
```

### 2. Validate in Compact Constructor
```java
public record Email(String value) {
    public Email {
        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}
```

### 3. Combine with Bean Validation
```java
public record CreateUserRequest(
    @NotBlank @Size(min = 3) String username,
    @Email String email
) {}
```

### 4. Use Static Factory Methods
```java
public record UserDTO(Long id, String username, String email) {
    public static UserDTO from(User entity) {
        return new UserDTO(entity.getId(), entity.getUsername(), entity.getEmail());
    }
}
```

### 5. Keep Records Simple
Don't add too much logic to Records. If you need complex behavior, use a regular class.

## Important Differences from Regular Classes

### 1. Accessor Methods Have NO 'get' Prefix
```java
public record UserDTO(String username) {}

// Correct
String name = user.username();

// WRONG - does not exist!
String name = user.getUsername();  // Compilation error!
```

### 2. All Fields are Final (Immutable)
```java
UserDTO user = new UserDTO(1L, "john", "john@example.com", "USER");
user.username = "jane";  // Compilation error - no setters!
```

### 3. Records are Final (Cannot Be Extended)
```java
// This won't compile
public record UserDTO(...) {}
public class ExtendedUserDTO extends UserDTO {} // ERROR!
```

### 4. Compact Constructor Syntax
```java
// Traditional constructor
public UserDTO(Long id, String username) {
    this.id = id;
    this.username = username;
}

// Compact constructor (only in Records)
public UserDTO {
    // id and username are implicit
    // Just validation/normalization here
}
```

## Jackson Serialization/Deserialization

Records work seamlessly with Jackson (Spring Boot's default JSON library):

**Serialization (Java → JSON):**
```java
UserDTO user = new UserDTO(1L, "john", "john@example.com", "USER");
// JSON: {"id":1,"username":"john","email":"john@example.com","role":"USER"}
```

**Deserialization (JSON → Java):**
```json
{
  "username": "john",
  "email": "john@example.com",
  "role": "USER"
}
```
Jackson automatically calls the Record's canonical constructor.

## Common Pitfalls

### 1. Forgetting NO 'get' Prefix
```java
// Wrong
user.getUsername()

// Correct
user.username()
```

### 2. Trying to Modify Records
```java
// Records are immutable - create a new one instead
UserDTO updated = new UserDTO(
    user.id(),
    "newUsername",  // changed
    user.email(),
    user.role()
);
```

### 3. Using Records for JPA Entities
```java
// WRONG - JPA entities need setters
@Entity
public record User(Long id, String username) {}

// CORRECT - Use regular class for entities
@Entity
public class User {
    @Id
    private Long id;
    private String username;
    // getters, setters
}
```

## Learning Resources

- **Java Records Tutorial**: https://docs.oracle.com/en/java/javase/17/language/records.html
- **JEP 395**: https://openjdk.org/jeps/395
- **Records vs Lombok**: Search "Java Records vs Lombok"

## Next Steps

After understanding Records, explore:
1. **Pattern Matching with Records** (Java 16+)
2. **Sealed Classes** (Java 17) - combine with Records
3. **MapStruct** - for Record DTO mapping

---

**Key Takeaway**: Java Records are the modern, recommended way to create DTOs in Java 16+. They're built into the language, require zero dependencies, and reduce boilerplate by 90%. In modern Spring Boot applications, prefer Records over traditional DTOs for all immutable data transfer objects.
