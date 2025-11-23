package com.example.injection.controller;

import com.example.injection.domain.User;
import com.example.injection.service.constructor.ConstructorInjectionUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ✅ Controller using CONSTRUCTOR INJECTION (MODERN WAY)
 */
@RestController
@RequestMapping("/api/constructor/users")
public class ConstructorInjectionUserController {

    // ✅ MODERN WAY: final field with constructor injection
    private final ConstructorInjectionUserService userService;

    /**
     * Constructor injection
     * @Autowired is optional (Spring 4.3+)
     */
    public ConstructorInjectionUserController(
            @Qualifier("constructorInjectionUserService") ConstructorInjectionUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestParam String username, @RequestParam String email) {
        User user = userService.createUser(username, email);
        return ResponseEntity.ok(user);
    }
}
