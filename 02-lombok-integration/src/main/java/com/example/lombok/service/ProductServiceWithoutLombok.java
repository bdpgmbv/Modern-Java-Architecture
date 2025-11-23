package com.example.lombok.service;

import com.example.lombok.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service class WITHOUT Lombok - showing verbose dependency injection and logging.
 *
 * Problems without Lombok:
 * 1. Manual logger initialization - boilerplate
 * 2. Verbose constructor for dependency injection
 * 3. Manual logging statements are verbose
 * 4. More code to maintain
 */
@Service
public class ProductServiceWithoutLombok {

    // Manual logger initialization - boilerplate code
    private static final Logger logger = Logger.getLogger(ProductServiceWithoutLombok.class.getName());

    // In-memory storage
    private final List<Product> products = new ArrayList<>();
    private Long nextId = 1L;

    // Manual constructor - required for dependency injection
    // Even with no dependencies, we need this for clarity
    public ProductServiceWithoutLombok() {
        logger.info("ProductServiceWithoutLombok initialized");
        initializeProducts();
    }

    private void initializeProducts() {
        // Initialize with sample data
        Product laptop = Product.builder()
                .id(nextId++)
                .name("Laptop")
                .description("Gaming laptop")
                .price(new BigDecimal("1299.99"))
                .build();
        products.add(laptop);

        Product mouse = Product.builder()
                .id(nextId++)
                .name("Mouse")
                .description("Wireless mouse")
                .price(new BigDecimal("29.99"))
                .build();
        products.add(mouse);

        logger.info("Initialized " + products.size() + " products");
    }

    public List<Product> getAllProducts() {
        logger.info("Fetching all products. Count: " + products.size());
        return new ArrayList<>(products);
    }

    public Optional<Product> getProductById(Long id) {
        logger.info("Fetching product with id: " + id);
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Product createProduct(Product product) {
        product.setId(nextId++);
        products.add(product);
        logger.info("Created product: " + product);
        return product;
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        logger.info("Updating product with id: " + id);
        Optional<Product> existingProduct = getProductById(id);

        existingProduct.ifPresent(product -> {
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            logger.info("Product updated: " + product);
        });

        return existingProduct;
    }

    public boolean deleteProduct(Long id) {
        logger.info("Deleting product with id: " + id);
        boolean removed = products.removeIf(p -> p.getId().equals(id));
        if (removed) {
            logger.info("Product deleted successfully");
        } else {
            logger.warning("Product not found with id: " + id);
        }
        return removed;
    }
}
