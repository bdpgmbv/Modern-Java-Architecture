package com.example.injection.service;

import com.example.injection.domain.User;
import com.example.injection.repository.UserRepository;
import com.example.injection.service.constructor.ConstructorInjectionUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ✅ EASY TO TEST with Constructor Injection
 *
 * No Spring context needed!
 * No reflection needed!
 * Just create mocks and new instance!
 */
class ConstructorInjectionUserServiceTest {

    private UserRepository mockRepository;
    private ConstructorInjectionUserService userService;

    @BeforeEach
    void setUp() {
        // Create mock repository
        mockRepository = mock(UserRepository.class);

        // ✅ EASY: Just create a new instance with mock!
        userService = new ConstructorInjectionUserService(mockRepository);
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        // Arrange
        User user = new User(1L, "testuser", "test@example.com");
        when(mockRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser("testuser", "test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(mockRepository).save(any(User.class));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        User user = new User(1L, "testuser", "test@example.com");
        when(mockRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(mockRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserById(999L);
        });
    }
}
