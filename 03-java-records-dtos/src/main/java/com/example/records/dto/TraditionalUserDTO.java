package com.example.records.dto;

import java.util.Objects;

/**
 * Traditional DTO class WITHOUT Java Records - showing verbose boilerplate code.
 *
 * This is how we used to write DTOs before Java Records.
 * Notice the amount of boilerplate required:
 * - Private final fields (for immutability)
 * - Constructor
 * - Getters (no setters - immutable)
 * - equals()
 * - hashCode()
 * - toString()
 *
 * This class has 70+ lines for just 4 fields!
 */
public class TraditionalUserDTO {

    private final Long id;
    private final String username;
    private final String email;
    private final String role;

    // Constructor
    public TraditionalUserDTO(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters (no setters - immutable)
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    // equals() - required for comparing DTOs
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TraditionalUserDTO that = (TraditionalUserDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(role, that.role);
    }

    // hashCode() - required for collections
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, role);
    }

    // toString() - useful for logging/debugging
    @Override
    public String toString() {
        return "TraditionalUserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    // Problems with this approach:
    // 1. 70+ lines of boilerplate
    // 2. Easy to make mistakes in equals/hashCode
    // 3. Adding new fields requires updating 5+ methods
    // 4. Verbose and hard to read
    // 5. More code to test and maintain
}
