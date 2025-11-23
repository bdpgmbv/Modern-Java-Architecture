package com.example.injection.controller;

import com.example.injection.domain.User;
import com.example.injection.service.field.FieldInjectionUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ❌ Controller using FIELD INJECTION (OLD WAY)
 */
@RestController
@RequestMapping("/api/field/users")
public class FieldInjectionUserController {

    // ❌ OLD WAY: Field injection
    @Autowired
    @Qualifier("fieldInjectionUserService")
    private FieldInjectionUserService userService;

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
