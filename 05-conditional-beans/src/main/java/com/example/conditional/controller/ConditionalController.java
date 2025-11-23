package com.example.conditional.controller;

import com.example.conditional.config.ConditionalBeansConfig;
import com.example.conditional.feature.FeatureService;
import com.example.conditional.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/conditional")
public class ConditionalController {

    private final CacheService cacheService;
    private final ApplicationContext context;

    @Autowired(required = false)
    private FeatureService featureService;

    @Autowired(required = false)
    private ConditionalBeansConfig.DataProcessor dataProcessor;

    public ConditionalController(CacheService cacheService, ApplicationContext context) {
        this.cacheService = cacheService;
        this.context = context;
    }

    @GetMapping("/cache")
    public Map<String, String> getCacheInfo() {
        return Map.of(
                "cacheType", cacheService.getCacheType(),
                "note", "Cache type determined by 'cache.type' property"
        );
    }

    @GetMapping("/features")
    public Map<String, Object> getFeatures() {
        Map<String, Object> result = new HashMap<>();
        result.put("analytics", featureService != null ? featureService.getStatus() : "DISABLED");
        result.put("note", "Features controlled by 'features.*' properties");
        return result;
    }

    @GetMapping("/all-beans")
    public Map<String, Object> getAllConditionalBeans() {
        Map<String, Object> beans = new HashMap<>();
        beans.put("cacheService", cacheService.getCacheType());
        beans.put("featureService", featureService != null ? "ENABLED" : "DISABLED");
        beans.put("dataProcessor", dataProcessor != null ? dataProcessor.getType() : "NONE");

        // Check for profile beans
        try {
            String devConfig = context.getBean("devConfig", String.class);
            beans.put("devConfig", devConfig);
        } catch (Exception e) {
            beans.put("devConfig", "NOT LOADED (not in dev profile)");
        }

        return beans;
    }
}
