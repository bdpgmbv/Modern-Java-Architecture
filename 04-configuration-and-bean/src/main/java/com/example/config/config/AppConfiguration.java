package com.example.config.config;

import com.example.config.service.MessageService;
import com.example.config.service.PushMessageService;
import com.example.config.service.SmsMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class demonstrating @Configuration and @Bean.
 *
 * @Configuration indicates that this class contains @Bean definitions.
 *
 * Key differences from @Component:
 * - @Configuration is for infrastructure/configuration beans
 * - @Component is for business logic beans
 * - @Configuration classes are CGLIB-proxied (ensures singleton for @Bean methods)
 * - @Bean methods allow programmatic bean creation
 *
 * When to use @Configuration + @Bean:
 * 1. Third-party library classes (can't add @Component)
 * 2. Beans requiring constructor parameters
 * 3. Conditional bean creation
 * 4. Complex initialization logic
 * 5. Multiple beans from same class
 */
@Configuration
public class AppConfiguration {

    /**
     * Inject properties from application.properties
     */
    @Value("${app.name:MyApp}")
    private String appName;

    @Value("${app.debug:false}")
    private boolean debugMode;

    @Value("${sms.provider:Twilio}")
    private String smsProvider;

    /**
     * Create SmsMessageService bean programmatically.
     *
     * Method name becomes the bean name (unless specified otherwise).
     * Bean name: "smsMessageService"
     *
     * Use @Bean when:
     * - Class needs constructor parameters
     * - Class is from external library
     * - Need custom initialization
     */
    @Bean
    public MessageService smsMessageService() {
        // Custom initialization logic
        System.out.println("Creating SMS service with provider from config: " + smsProvider);

        // Pass constructor parameters
        return new SmsMessageService(smsProvider);
    }

    /**
     * Create PushMessageService bean and mark it as @Primary.
     *
     * @Primary indicates this is the DEFAULT bean when multiple
     * implementations of MessageService exist.
     *
     * When Spring sees:
     *   @Autowired MessageService service;
     *
     * It will inject THIS bean by default (because of @Primary).
     *
     * To inject other implementations, use @Qualifier("beanName")
     */
    @Bean
    @Primary  // This is the DEFAULT MessageService
    public MessageService pushMessageService() {
        System.out.println("Creating Push service (PRIMARY) with app config");

        // Complex initialization logic
        return new PushMessageService(appName, debugMode);
    }

    /**
     * Alternative: Explicit bean name.
     *
     * You can specify bean name explicitly:
     * @Bean(name = "customServiceName")
     *
     * Or multiple names:
     * @Bean({"name1", "name2"})
     */
    @Bean(name = "notificationService")
    public MessageService customNamedService() {
        return new PushMessageService("CustomApp", false);
    }

    // Note: EmailMessageService is created via @Component,
    // so we don't need a @Bean method for it.

    /**
     * Example: Creating a third-party object as a bean.
     *
     * Imagine this is from a library you don't control.
     */
    @Bean
    public String applicationVersion() {
        // Could read from manifest, git, etc.
        return "1.0.0";
    }

    /**
     * Example: Bean with dependencies.
     *
     * @Bean methods can have parameters - Spring auto-injects them!
     */
    @Bean
    public String welcomeMessage(String applicationVersion) {
        return String.format("Welcome to %s v%s", appName, applicationVersion);
    }
}
