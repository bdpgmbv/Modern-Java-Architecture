package com.example.records.controller;

import com.example.records.dto.CreateUserRequest;
import com.example.records.dto.UserDTO;
import com.example.records.dto.UserResponseDTO;
import com.example.records.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller demonstrating Java Records as DTOs.
 *
 * Key Points:
 * 1. Use @Valid with Record DTOs for validation
 * 2. Records work seamlessly with @RequestBody
 * 3. Jackson serializes/deserializes Records automatically
 * 4. Records have no 'get' prefix in accessors: user.username() not user.getUsername()
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users
     * Returns a list of UserDTO Records
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id}
     * Returns a single UserResponseDTO Record
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * GET /api/users/search?role=ADMIN
     * Search users by role
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchByRole(@RequestParam String role) {
        List<UserDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * POST /api/users
     * Create user with validation.
     *
     * @Valid triggers validation on CreateUserRequest record
     * The compact constructor also performs additional validation
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        // The CreateUserRequest record has already:
        // 1. Been validated by @Valid (Bean Validation annotations)
        // 2. Been validated by compact constructor (custom validation)
        // 3. Normalized the data (lowercase username/email, uppercase role)

        UserResponseDTO created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/users/{id}
     * Update user with validation
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request) {

        return userService.updateUser(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/users/{id}
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Exception handler for validation errors.
     *
     * This catches validation errors from @Valid annotation
     * and returns a clean error response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Exception handler for IllegalArgumentException.
     *
     * This catches validation errors from compact constructor.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}
