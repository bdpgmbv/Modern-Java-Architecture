package com.example.injection.service.constructor;

import com.example.injection.domain.User;
import com.example.injection.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ✅ MODERN WAY: Constructor Injection (RECOMMENDED)
 *
 * BENEFITS of Constructor Injection:
 * 1. ✅ Fields can be final (immutable)
 * 2. ✅ Easy to test without Spring container
 * 3. ✅ All dependencies are clearly visible in constructor
 * 4. ✅ Cannot create instance without dependencies (compile-time safety)
 * 5. ✅ Follows SOLID principles
 * 6. ✅ Circular dependencies detected at startup
 * 7. ✅ No reflection needed for testing
 *
 * This is the MODERN WAY - USE THIS!
 *
 * Note: @Autowired is optional on constructor (Spring 4.3+)
 */
@Service("constructorInjectionUserService")
public class ConstructorInjectionUserService {

    // ✅ MODERN WAY: final field (immutable)
    private final UserRepository userRepository;

    /**
     * Constructor injection
     * @Autowired is OPTIONAL when there's only one constructor
     */
    public ConstructorInjectionUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        // userRepository is guaranteed to be non-null here!
    }

    public User createUser(String username, String email) {
        User user = new User(null, username, email);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }
}
