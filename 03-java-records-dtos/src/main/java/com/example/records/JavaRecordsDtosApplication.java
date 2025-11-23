package com.example.records;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application demonstrating Java Records (Java 14+).
 *
 * Java Records are a special kind of class designed for immutable data carriers.
 * They are perfect for DTOs (Data Transfer Objects) because they:
 * - Are immutable by default
 * - Auto-generate constructor, getters, equals, hashCode, toString
 * - Reduce boilerplate by 90%
 * - Are more concise than Lombok
 * - Are built into Java (no external dependencies)
 *
 * This project compares:
 * 1. Traditional DTO (50+ lines)
 * 2. Modern Record DTO (5 lines)
 * 3. Records with validation
 * 4. Compact constructors
 */
@SpringBootApplication
public class JavaRecordsDtosApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaRecordsDtosApplication.class, args);
    }
}
