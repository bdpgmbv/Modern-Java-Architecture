package com.example.config.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * FactoryBean example - Advanced bean creation pattern.
 *
 * FactoryBean is used when:
 * 1. Bean creation is complex (multi-step initialization)
 * 2. Need to create different types based on configuration
 * 3. Need lifecycle hooks (initialization, destruction)
 * 4. Creating objects from external libraries with complex setup
 *
 * How it works:
 * - Spring calls getObject() to create the actual bean
 * - The bean returned by getObject() is what gets injected
 * - To inject the FactoryBean itself, use: @Autowired &beanName
 *
 * Example use cases:
 * - Database connection pools
 * - HTTP clients with complex configuration
 * - Third-party library objects
 * - Proxy objects
 */
@Component
public class DatabaseConnectionFactoryBean implements FactoryBean<DatabaseConnection> {

    @Value("${db.url:jdbc:postgresql://localhost:5432/mydb}")
    private String dbUrl;

    @Value("${db.username:admin}")
    private String dbUsername;

    @Value("${db.max-connections:10}")
    private int maxConnections;

    @Value("${db.pooling:true}")
    private boolean poolingEnabled;

    /**
     * This method is called by Spring to create the actual bean.
     *
     * You can add any complex initialization logic here:
     * - Validation
     * - Connection testing
     * - Resource allocation
     * - Multi-step setup
     */
    @Override
    public DatabaseConnection getObject() throws Exception {
        System.out.println("FactoryBean: Creating DatabaseConnection...");

        // Step 1: Validate configuration
        validateConfiguration();

        // Step 2: Perform initialization logic
        initializeConnectionPool();

        // Step 3: Create and return the actual object
        DatabaseConnection connection = new DatabaseConnection(
                dbUrl,
                dbUsername,
                maxConnections,
                poolingEnabled
        );

        // Step 4: Post-creation setup
        System.out.println("FactoryBean: DatabaseConnection created successfully!");

        return connection;
    }

    /**
     * Return the type of object this factory creates.
     * Spring uses this for type matching during autowiring.
     */
    @Override
    public Class<?> getObjectType() {
        return DatabaseConnection.class;
    }

    /**
     * Is this bean a singleton?
     * - true: Spring creates one instance (default)
     * - false: Spring creates new instance each time
     */
    @Override
    public boolean isSingleton() {
        return true;  // One database connection pool per application
    }

    // ========== Helper Methods ==========

    private void validateConfiguration() {
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalStateException("Database URL cannot be empty");
        }

        if (maxConnections <= 0) {
            throw new IllegalStateException("Max connections must be positive");
        }

        System.out.println("FactoryBean: Configuration validated");
    }

    private void initializeConnectionPool() {
        // Simulate complex initialization
        System.out.println("FactoryBean: Initializing connection pool...");

        if (poolingEnabled) {
            System.out.println("FactoryBean: Setting up connection pooling with max " + maxConnections);
        }

        // In real scenario: create pool, test connection, etc.
    }
}
