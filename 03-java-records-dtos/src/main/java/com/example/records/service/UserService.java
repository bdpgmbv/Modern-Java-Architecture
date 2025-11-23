package com.example.records.service;

import com.example.records.dto.CreateUserRequest;
import com.example.records.dto.UserDTO;
import com.example.records.dto.UserResponseDTO;
import com.example.records.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service demonstrating how to work with Records as DTOs.
 *
 * Best Practice:
 * - Use Entity classes (User) for business logic and persistence
 * - Use Record DTOs (UserDTO, UserResponseDTO) for API communication
 * - Convert between entities and Records in the service layer
 */
@Service
public class UserService {

    private final List<User> users = new ArrayList<>();
    private Long nextId = 1L;

    public UserService() {
        // Initialize with sample data
        User admin = new User(nextId++, "admin", "admin@example.com", "password123", "ADMIN");
        User user1 = new User(nextId++, "john", "john@example.com", "password123", "USER");
        User user2 = new User(nextId++, "jane", "jane@example.com", "password123", "MODERATOR");

        users.add(admin);
        users.add(user1);
        users.add(user2);
    }

    /**
     * Get all users as DTOs (Records).
     *
     * Converting Entity -> Record DTO
     */
    public List<UserDTO> getAllUsers() {
        return users.stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Get user by ID as a Record DTO.
     */
    public Optional<UserResponseDTO> getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .map(this::convertToResponseDTO)
                .findFirst();
    }

    /**
     * Create user from Record DTO.
     *
     * Converting Record DTO -> Entity
     *
     * Notice how the compact constructor in CreateUserRequest
     * automatically validates and normalizes the input!
     */
    public UserResponseDTO createUser(CreateUserRequest request) {
        // The CreateUserRequest record has already validated and normalized the data
        // in its compact constructor!

        User user = new User(
                nextId++,
                request.username(),  // Already normalized (lowercase, trimmed)
                request.email(),     // Already normalized (lowercase, trimmed)
                request.password(),
                request.role()       // Already normalized (uppercase, trimmed)
        );

        users.add(user);

        return convertToResponseDTO(user);
    }

    /**
     * Update user.
     */
    public Optional<UserResponseDTO> updateUser(Long id, CreateUserRequest request) {
        Optional<User> existingUser = users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();

        existingUser.ifPresent(user -> {
            user.setUsername(request.username());
            user.setEmail(request.email());
            user.setPassword(request.password());
            user.setRole(request.role());
        });

        return existingUser.map(this::convertToResponseDTO);
    }

    /**
     * Delete user.
     */
    public boolean deleteUser(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }

    /**
     * Search users by role - demonstrates working with Record accessors.
     *
     * Note: Record accessors don't have 'get' prefix!
     * Use: userDTO.role() NOT userDTO.getRole()
     */
    public List<UserDTO> getUsersByRole(String role) {
        return users.stream()
                .filter(user -> user.getRole().equalsIgnoreCase(role))
                .map(this::convertToDTO)
                .toList();
    }

    // ========== Conversion Methods ==========

    /**
     * Convert Entity to simple DTO Record.
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * Convert Entity to response DTO Record.
     *
     * Alternatively, could use the static factory method:
     * return UserResponseDTO.from(convertToDTO(user));
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
