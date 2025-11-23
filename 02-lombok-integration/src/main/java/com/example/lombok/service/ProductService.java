package com.example.lombok.service;

import com.example.lombok.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class WITH Lombok - clean and concise!
 *
 * @Slf4j - Lombok annotation that generates a logger field:
 *   private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductService.class);
 *
 * Benefits:
 * 1. No manual logger initialization
 * 2. Automatic logger instance named 'log'
 * 3. Uses SLF4J (industry standard)
 * 4. Less boilerplate code
 * 5. Can use log.info(), log.debug(), log.error(), etc.
 *
 * Compare this class with ProductServiceWithoutLombok to see the difference!
 */
@Slf4j  // Generates logger automatically!
@Service
public class ProductService {

    // In-memory storage
    private final List<Product> products = new ArrayList<>();
    private Long nextId = 1L;

    public ProductService() {
        // Using Lombok's generated logger - much cleaner!
        log.info("ProductService initialized with Lombok @Slf4j");
        initializeProducts();
    }

    private void initializeProducts() {
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

        // Clean logging with Lombok
        log.info("Initialized {} products", products.size());
    }

    public List<Product> getAllProducts() {
        log.info("Fetching all products. Count: {}", products.size());
        return new ArrayList<>(products);
    }

    public Optional<Product> getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public Product createProduct(Product product) {
        product.setId(nextId++);
        products.add(product);
        log.info("Created product: {}", product);
        return product;
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        log.debug("Updating product with id: {}", id);
        Optional<Product> existingProduct = getProductById(id);

        existingProduct.ifPresent(product -> {
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            log.info("Product updated: {}", product);
        });

        return existingProduct;
    }

    public boolean deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);
        boolean removed = products.removeIf(p -> p.getId().equals(id));

        if (removed) {
            log.info("Product deleted successfully with id: {}", id);
        } else {
            log.warn("Product not found with id: {}", id);
        }

        return removed;
    }
}
