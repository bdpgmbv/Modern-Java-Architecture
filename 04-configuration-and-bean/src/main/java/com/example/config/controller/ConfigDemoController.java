package com.example.config.controller;

import com.example.config.factory.DatabaseConnection;
import com.example.config.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller demonstrating different ways to inject and use beans.
 *
 * Demonstrates:
 * 1. @Primary - default bean injection
 * 2. @Qualifier - specific bean injection by name
 * 3. FactoryBean - complex bean creation
 * 4. ApplicationContext - programmatic bean lookup
 */
@RestController
@RequestMapping("/api/config")
public class ConfigDemoController {

    /**
     * Injected via @Primary
     * Since PushMessageService is marked @Primary in AppConfiguration,
     * this will be PushMessageService.
     */
    private final MessageService defaultMessageService;

    /**
     * Injected via @Qualifier - specific bean by name
     * Bean name: "smsMessageService" (from @Bean method name)
     */
    private final MessageService smsService;

    /**
     * Injected via @Qualifier - specific bean by name
     * Bean name: "emailMessageService" (from @Component class name)
     */
    private final MessageService emailService;

    /**
     * Injected via @Qualifier - custom named bean
     * Bean name: "notificationService" (from @Bean(name = "..."))
     */
    private final MessageService notificationService;

    /**
     * Injected - created by FactoryBean
     * Spring automatically calls FactoryBean.getObject()
     */
    private final DatabaseConnection databaseConnection;

    /**
     * Injected - simple String bean
     */
    private final String welcomeMessage;

    /**
     * ApplicationContext - for programmatic bean lookup
     */
    private final ApplicationContext applicationContext;

    /**
     * Constructor injection demonstrating @Qualifier usage.
     *
     * Best Practice: Use constructor injection (not field injection)
     */
    @Autowired
    public ConfigDemoController(
            // @Primary bean is injected when no @Qualifier specified
            MessageService defaultMessageService,

            // Specific beans injected via @Qualifier
            @Qualifier("smsMessageService") MessageService smsService,
            @Qualifier("emailMessageService") MessageService emailService,
            @Qualifier("notificationService") MessageService notificationService,

            // FactoryBean-created object
            DatabaseConnection databaseConnection,

            // Simple bean
            String welcomeMessage,

            // ApplicationContext for programmatic lookups
            ApplicationContext applicationContext) {

        this.defaultMessageService = defaultMessageService;
        this.smsService = smsService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.databaseConnection = databaseConnection;
        this.welcomeMessage = welcomeMessage;
        this.applicationContext = applicationContext;
    }

    /**
     * GET /api/config/default
     * Uses @Primary bean
     */
    @GetMapping("/default")
    public Map<String, String> getDefaultService() {
        return Map.of(
                "service", defaultMessageService.getServiceType(),
                "message", defaultMessageService.getMessage(),
                "note", "This is the @Primary bean (injected by default)"
        );
    }

    /**
     * GET /api/config/sms
     * Uses @Qualifier("smsMessageService")
     */
    @GetMapping("/sms")
    public Map<String, String> getSmsService() {
        return Map.of(
                "service", smsService.getServiceType(),
                "message", smsService.getMessage(),
                "note", "Injected via @Qualifier(\"smsMessageService\") - created by @Bean"
        );
    }

    /**
     * GET /api/config/email
     * Uses @Qualifier("emailMessageService")
     */
    @GetMapping("/email")
    public Map<String, String> getEmailService() {
        return Map.of(
                "service", emailService.getServiceType(),
                "message", emailService.getMessage(),
                "note", "Injected via @Qualifier(\"emailMessageService\") - created by @Component"
        );
    }

    /**
     * GET /api/config/notification
     * Uses @Qualifier("notificationService")
     */
    @GetMapping("/notification")
    public Map<String, String> getNotificationService() {
        return Map.of(
                "service", notificationService.getServiceType(),
                "message", notificationService.getMessage(),
                "note", "Injected via @Qualifier(\"notificationService\") - custom bean name"
        );
    }

    /**
     * GET /api/config/database
     * Uses FactoryBean-created object
     */
    @GetMapping("/database")
    public Map<String, Object> getDatabaseInfo() {
        return Map.of(
                "connection", databaseConnection.getConnectionInfo(),
                "url", databaseConnection.getUrl(),
                "maxConnections", databaseConnection.getMaxConnections(),
                "pooling", databaseConnection.isPoolingEnabled(),
                "note", "Created by DatabaseConnectionFactoryBean"
        );
    }

    /**
     * GET /api/config/welcome
     * Uses simple String bean
     */
    @GetMapping("/welcome")
    public Map<String, String> getWelcome() {
        return Map.of(
                "message", welcomeMessage,
                "note", "Simple String bean created in @Configuration"
        );
    }

    /**
     * GET /api/config/all-services
     * Demonstrates programmatic bean lookup using ApplicationContext
     */
    @GetMapping("/all-services")
    public Map<String, Object> getAllServices() {
        // Programmatic bean lookup
        Map<String, MessageService> allServices = applicationContext.getBeansOfType(MessageService.class);

        Map<String, Object> result = new HashMap<>();
        result.put("totalBeans", allServices.size());
        result.put("beanNames", allServices.keySet());

        Map<String, String> serviceInfo = new HashMap<>();
        allServices.forEach((name, service) -> {
            serviceInfo.put(name, service.getServiceType());
        });
        result.put("services", serviceInfo);

        result.put("note", "All MessageService beans found via ApplicationContext.getBeansOfType()");

        return result;
    }

    /**
     * GET /api/config/bean-comparison
     * Compares @Component vs @Bean
     */
    @GetMapping("/bean-comparison")
    public Map<String, Object> getBeanComparison() {
        Map<String, Object> result = new HashMap<>();

        result.put("component_example", Map.of(
                "class", "EmailMessageService",
                "annotation", "@Component",
                "beanName", "emailMessageService",
                "creation", "Automatic via component scanning",
                "use_case", "Classes you own with simple initialization"
        ));

        result.put("bean_example", Map.of(
                "class", "SmsMessageService",
                "annotation", "@Bean in @Configuration",
                "beanName", "smsMessageService",
                "creation", "Programmatic in AppConfiguration",
                "use_case", "Third-party classes or complex initialization"
        ));

        result.put("primary_example", Map.of(
                "class", "PushMessageService",
                "annotation", "@Bean + @Primary",
                "beanName", "pushMessageService",
                "creation", "Programmatic with @Primary marker",
                "use_case", "Default implementation when multiple exist"
        ));

        result.put("factorybean_example", Map.of(
                "class", "DatabaseConnection",
                "annotation", "FactoryBean implementation",
                "creation", "Via DatabaseConnectionFactoryBean.getObject()",
                "use_case", "Complex multi-step bean creation"
        ));

        return result;
    }
}
