package com.example.config.service;

/**
 * Another implementation created via @Bean.
 *
 * This will be marked as @Primary to demonstrate default bean selection
 * when multiple implementations exist.
 */
public class PushMessageService implements MessageService {

    private final String appName;
    private final boolean debugMode;

    /**
     * Constructor with multiple parameters.
     * This requires @Bean for instantiation.
     */
    public PushMessageService(String appName, boolean debugMode) {
        this.appName = appName;
        this.debugMode = debugMode;
        System.out.println("PushMessageService created via @Bean - App: " + appName + ", Debug: " + debugMode);
    }

    @Override
    public String getMessage() {
        String mode = debugMode ? "[DEBUG]" : "[PROD]";
        return mode + " Sending push notification via " + appName;
    }

    @Override
    public String getServiceType() {
        return "Push Service (via @Bean - @Primary) - App: " + appName;
    }
}
