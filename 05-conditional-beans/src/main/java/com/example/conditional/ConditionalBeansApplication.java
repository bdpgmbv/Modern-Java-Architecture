package com.example.conditional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demonstrates Conditional Bean Creation in Spring Boot.
 *
 * Topics covered:
 * - @ConditionalOnProperty - based on application.properties
 * - @ConditionalOnBean / @ConditionalOnMissingBean
 * - @Profile - environment-based beans
 * - Feature flags via properties
 * - Custom @Conditional annotations
 */
@SpringBootApplication
public class ConditionalBeansApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConditionalBeansApplication.class, args);
    }
}
