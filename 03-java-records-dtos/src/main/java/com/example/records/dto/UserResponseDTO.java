package com.example.records.dto;

import java.time.LocalDateTime;

/**
 * Record with compact constructor demonstrating validation and defaults.
 *
 * Compact constructors are unique to Records and allow:
 * 1. Input validation
 * 2. Normalization
 * 3. Defensive copies
 * 4. Custom logic before field assignment
 *
 * The compact constructor has NO parameter list - it implicitly
 * has access to all record components.
 */
public record UserResponseDTO(
        Long id,
        String username,
        String email,
        String role,
        LocalDateTime createdAt
) {
    /**
     * Compact Constructor - validates and normalizes inputs
     */
    public UserResponseDTO {
        // Null checks
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }

        // Normalize data
        username = username.trim();
        email = email.toLowerCase().trim();
        role = role.toUpperCase().trim();

        // Default value if null
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Custom constructor with fewer parameters.
     * This is a regular constructor that delegates to the compact constructor.
     */
    public UserResponseDTO(Long id, String username, String email, String role) {
        this(id, username, email, role, LocalDateTime.now());
    }

    /**
     * Static factory method - another way to create Records
     */
    public static UserResponseDTO from(UserDTO userDTO) {
        return new UserResponseDTO(
                userDTO.id(),
                userDTO.username(),
                userDTO.email(),
                userDTO.role(),
                LocalDateTime.now()
        );
    }

    /**
     * Custom methods can be added to Records
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public String getDisplayName() {
        return username + " (" + role + ")";
    }

    /**
     * You can override generated methods if needed
     */
    @Override
    public String toString() {
        return String.format("User[id=%d, username=%s, role=%s]", id, username, role);
    }
}
