package com.example.injection.service;

import com.example.injection.domain.User;
import com.example.injection.repository.UserRepository;
import com.example.injection.service.field.FieldInjectionUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ❌ HARD TO TEST with Field Injection
 *
 * Need to use reflection to inject mocks!
 * More complex setup!
 * Alternative: Load entire Spring context (slow!)
 */
class FieldInjectionUserServiceTest {

    private UserRepository mockRepository;
    private FieldInjectionUserService userService;

    @BeforeEach
    void setUp() {
        // Create mock repository
        mockRepository = mock(UserRepository.class);

        // ❌ PROBLEM: Can't pass mock through constructor!
        userService = new FieldInjectionUserService();

        // ❌ UGLY SOLUTION: Use reflection to inject mock
        ReflectionTestUtils.setField(userService, "userRepository", mockRepository);

        /**
         * Alternative (even worse): Use @SpringBootTest
         *
         * @SpringBootTest
         * class FieldInjectionUserServiceTest {
         *     @Autowired
         *     private FieldInjectionUserService userService;
         *
         *     // This loads the ENTIRE Spring context - very slow!
         * }
         */
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

    /**
     * See the difference?
     * - Constructor injection: Just new Service(mockRepo)
     * - Field injection: Need ReflectionTestUtils or @SpringBootTest
     *
     * Constructor injection is MUCH cleaner for testing!
     */
}
