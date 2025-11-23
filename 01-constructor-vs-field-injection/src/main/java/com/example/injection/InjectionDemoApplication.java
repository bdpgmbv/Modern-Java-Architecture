package com.example.injection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo: Constructor Injection vs Field Injection
 *
 * This application demonstrates the difference between:
 * - Field Injection (OLD, NOT RECOMMENDED)
 * - Constructor Injection (MODERN, RECOMMENDED)
 */
@SpringBootApplication
public class InjectionDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(InjectionDemoApplication.class, args);
    }
}
