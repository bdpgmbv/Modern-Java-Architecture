package com.example.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application demonstrating @Configuration and @Bean.
 *
 * This project showcases:
 * 1. @Configuration - Class-level annotation for bean definitions
 * 2. @Bean - Method-level annotation for programmatic bean creation
 * 3. @Component vs @Bean - When to use each
 * 4. @Primary - Default bean when multiple implementations exist
 * 5. @Qualifier - Selecting specific bean by name
 * 6. FactoryBean - Advanced bean creation pattern
 *
 * Key Concepts:
 * - @Component: For classes YOU control (automatic bean detection)
 * - @Bean: For classes you DON'T control or need custom initialization
 * - @Configuration: Container for @Bean methods
 * - @Primary: Default bean when multiple candidates exist
 * - FactoryBean: Complex bean creation with lifecycle control
 */
@SpringBootApplication
public class ConfigurationAndBeanApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationAndBeanApplication.class, args);
    }
}
