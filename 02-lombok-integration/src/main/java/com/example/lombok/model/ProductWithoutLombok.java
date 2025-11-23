package com.example.lombok.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Traditional Java class WITHOUT Lombok - showing verbose boilerplate code.
 *
 * Notice how much code is needed for a simple POJO:
 * - Manual fields declaration
 * - Manual constructor
 * - Manual getters
 * - Manual setters
 * - Manual toString()
 * - Manual equals() and hashCode()
 *
 * This class has ~80 lines of code for just 4 fields!
 */
public class ProductWithoutLombok {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    // Default constructor
    public ProductWithoutLombok() {
    }

    // All-args constructor
    public ProductWithoutLombok(Long id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // toString()
    @Override
    public String toString() {
        return "ProductWithoutLombok{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }

    // equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductWithoutLombok that = (ProductWithoutLombok) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(price, that.price);
    }

    // hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price);
    }
}
