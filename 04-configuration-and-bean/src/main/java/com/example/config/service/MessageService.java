package com.example.config.service;

/**
 * Service interface demonstrating multiple implementations.
 *
 * This interface will have multiple implementations to showcase:
 * - @Component vs @Bean
 * - @Primary for default implementation
 * - @Qualifier for specific bean selection
 */
public interface MessageService {

    /**
     * Get a message from the service.
     */
    String getMessage();

    /**
     * Get the service type/name.
     */
    String getServiceType();
}
