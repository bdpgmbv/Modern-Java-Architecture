package com.example.lombok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class demonstrating Lombok integration in Spring Boot.
 *
 * This project showcases how Lombok reduces boilerplate code by auto-generating:
 * - Constructors (@RequiredArgsConstructor, @AllArgsConstructor, @NoArgsConstructor)
 * - Getters and Setters (@Getter, @Setter, @Data)
 * - toString, equals, hashCode (@ToString, @EqualsAndHashCode, @Data)
 * - Builder pattern (@Builder)
 * - Logging (@Slf4j)
 */
@SpringBootApplication
public class LombokIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LombokIntegrationApplication.class, args);
    }
}
