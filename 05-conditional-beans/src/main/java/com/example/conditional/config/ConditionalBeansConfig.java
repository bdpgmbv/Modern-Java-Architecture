package com.example.conditional.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Demonstrates various conditional bean creation patterns.
 */
@Configuration
public class ConditionalBeansConfig {

    /**
     * @ConditionalOnMissingBean - Create ONLY if no other DataProcessor exists
     * This is a "fallback" bean.
     */
    @Bean
    @ConditionalOnMissingBean(DataProcessor.class)
    public DataProcessor defaultDataProcessor() {
        System.out.println("✓ DefaultDataProcessor created (no other DataProcessor found)");
        return new DataProcessor("Default");
    }

    /**
     * @Profile("dev") - Create ONLY in dev profile
     * Activate with: --spring.profiles.active=dev
     */
    @Bean
    @Profile("dev")
    public String devConfig() {
        System.out.println("✓ Dev configuration loaded");
        return "Development Configuration";
    }

    /**
     * @Profile("prod") - Create ONLY in prod profile
     */
    @Bean
    @Profile("prod")
    public String prodConfig() {
        System.out.println("✓ Production configuration loaded");
        return "Production Configuration";
    }

    /**
     * @Profile({"dev", "test"}) - Create in multiple profiles
     */
    @Bean
    @Profile({"dev", "test"})
    public String debugMode() {
        return "Debug Mode Enabled";
    }

    /**
     * @ConditionalOnBean - Create ONLY if another bean exists
     */
    @Bean
    @ConditionalOnBean(name = "devConfig")
    public String devLogger() {
        return "Dev Logger (created because devConfig exists)";
    }

    public static class DataProcessor {
        private final String type;

        public DataProcessor(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
