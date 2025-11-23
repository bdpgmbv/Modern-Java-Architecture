package com.example.config.service;

/**
 * Implementation WITHOUT @Component - will be created via @Bean.
 *
 * Notice: No Spring annotations!
 * This is a POJO (Plain Old Java Object).
 *
 * Use @Bean (instead of @Component) when:
 * 1. Class is from a third-party library (can't add @Component)
 * 2. Need custom initialization logic
 * 3. Need to pass constructor parameters
 * 4. Need conditional bean creation
 * 5. Need to create multiple beans from same class
 *
 * This class will be instantiated in AppConfiguration using @Bean.
 */
public class SmsMessageService implements MessageService {

    private final String providerName;

    /**
     * Constructor with parameters.
     * Cannot use @Component if constructor needs parameters!
     */
    public SmsMessageService(String providerName) {
        this.providerName = providerName;
        System.out.println("SmsMessageService created via @Bean with provider: " + providerName);
    }

    @Override
    public String getMessage() {
        return "Sending message via SMS using " + providerName;
    }

    @Override
    public String getServiceType() {
        return "SMS Service (via @Bean) - Provider: " + providerName;
    }
}
