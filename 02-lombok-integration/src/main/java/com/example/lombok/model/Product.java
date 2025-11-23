package com.example.lombok.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Modern Java class WITH Lombok - concise and clean!
 *
 * @Data - Generates:
 *   - Getters for all fields
 *   - Setters for all non-final fields
 *   - toString()
 *   - equals() and hashCode()
 *   - RequiredArgsConstructor (for final fields)
 *
 * @NoArgsConstructor - Generates a no-argument constructor
 *
 * @AllArgsConstructor - Generates a constructor with all fields as parameters
 *
 * @Builder - Implements the Builder pattern:
 *   Product product = Product.builder()
 *       .id(1L)
 *       .name("Laptop")
 *       .description("Gaming laptop")
 *       .price(new BigDecimal("1299.99"))
 *       .build();
 *
 * This class has ~25 lines vs ~80 lines without Lombok!
 * Same functionality, 70% less code!
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    // That's it! Lombok generates everything else at compile time.
    // No manual getters, setters, constructors, toString, equals, or hashCode needed!
}
