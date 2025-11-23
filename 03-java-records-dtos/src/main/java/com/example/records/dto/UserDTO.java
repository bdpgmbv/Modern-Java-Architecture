package com.example.records.dto;

/**
 * Modern DTO using Java Record - concise and clean!
 *
 * Java Records (introduced in Java 14, finalized in Java 16) are designed
 * for immutable data carriers like DTOs.
 *
 * This single line is equivalent to the 70+ lines in TraditionalUserDTO!
 *
 * What Java generates automatically:
 * 1. Private final fields
 * 2. Public constructor
 * 3. Public accessor methods (not getters - no 'get' prefix!)
 * 4. equals() method
 * 5. hashCode() method
 * 6. toString() method
 *
 * Benefits:
 * - 90% less code
 * - Immutable by default (all fields are final)
 * - Null-safe (can add validation in compact constructor)
 * - Built into Java (no Lombok needed)
 * - Perfect for DTOs, API responses, value objects
 *
 * Syntax: record Name(Type field1, Type field2, ...) {}
 */
public record UserDTO(
        Long id,
        String username,
        String email,
        String role
) {
    // That's it! Everything else is generated automatically.

    // Note: Accessor methods don't have 'get' prefix
    // Use: userDTO.username() NOT userDTO.getUsername()
}
