package com.example.config.service;

import org.springframework.stereotype.Component;

/**
 * Implementation using @Component - Automatic bean detection.
 *
 * Use @Component when:
 * 1. You OWN the class (it's in your codebase)
 * 2. You don't need custom initialization logic
 * 3. Default constructor is sufficient
 * 4. You want Spring to auto-detect and create the bean
 *
 * Spring will automatically:
 * - Scan this class during component scanning
 * - Create a singleton bean named "emailMessageService" (default name)
 * - Inject it wherever MessageService is required
 */
@Component
public class EmailMessageService implements MessageService {

    public EmailMessageService() {
        System.out.println("EmailMessageService created via @Component");
    }

    @Override
    public String getMessage() {
        return "Sending message via Email";
    }

    @Override
    public String getServiceType() {
        return "Email Service (via @Component)";
    }
}
