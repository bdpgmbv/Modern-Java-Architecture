package com.example.injection.service.field;

import com.example.injection.domain.User;
import com.example.injection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ❌ OLD WAY: Field Injection (NOT RECOMMENDED)
 *
 * PROBLEMS with Field Injection:
 * 1. ❌ Cannot make fields final (mutable state)
 * 2. ❌ Hard to test without Spring container
 * 3. ❌ Hidden dependencies - not clear what's required
 * 4. ❌ Can create instance without dependencies (NullPointerException risk)
 * 5. ❌ Violates dependency inversion principle
 * 6. ❌ Harder to detect circular dependencies
 * 7. ❌ Need reflection or Spring context for testing
 *
 * This is the OLD WAY - DO NOT USE in new code!
 */
@Service("fieldInjectionUserService")
public class FieldInjectionUserService {

    // ❌ OLD WAY: Field Injection with @Autowired
    @Autowired
    private UserRepository userRepository;

    /**
     * PROBLEM: You can create an instance without dependencies!
     * This will fail at runtime with NullPointerException
     */
    public FieldInjectionUserService() {
        // Dependencies are injected AFTER construction
        // Cannot use userRepository here - it's still null!
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
