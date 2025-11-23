package com.example.records.model;

import java.time.LocalDateTime;

/**
 * User entity - NOT a Record because entities need to be mutable.
 *
 * When NOT to use Records:
 * 1. JPA/Hibernate entities (need setters, no-arg constructor)
 * 2. Mutable objects
 * 3. Objects with inheritance
 * 4. Objects requiring lazy initialization
 *
 * Use Records for:
 * - DTOs (Data Transfer Objects)
 * - API request/response objects
 * - Value objects
 * - Immutable configuration
 */
public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(Long id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters (needed for entities)
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
