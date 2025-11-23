package com.example.records.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Record with Bean Validation annotations.
 *
 * Records work seamlessly with Spring Boot's validation framework.
 * You can use standard validation annotations:
 * - @NotBlank
 * - @NotNull
 * - @Email
 * - @Size
 * - @Pattern
 * - etc.
 *
 * Spring will automatically validate these when using @Valid in controllers.
 */
public record CreateUserRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Role is required")
        String role
) {
    /**
     * Compact Constructor - special to Records
     *
     * This is NOT a regular constructor!
     * It runs BEFORE field assignment and allows:
     * - Input validation
     * - Normalization/transformation
     * - Custom logic
     *
     * You don't need to assign fields - Java does it automatically!
     */
    public CreateUserRequest {
        // Normalize username to lowercase
        username = username.trim().toLowerCase();

        // Normalize email to lowercase
        email = email.trim().toLowerCase();

        // Normalize role to uppercase
        role = role.trim().toUpperCase();

        // Additional validation (beyond annotations)
        if (username.contains(" ")) {
            throw new IllegalArgumentException("Username cannot contain spaces");
        }

        if (!role.matches("USER|ADMIN|MODERATOR")) {
            throw new IllegalArgumentException("Role must be USER, ADMIN, or MODERATOR");
        }
    }

    // You can also add custom methods to Records
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public String getMaskedPassword() {
        return "*".repeat(password.length());
    }
}
