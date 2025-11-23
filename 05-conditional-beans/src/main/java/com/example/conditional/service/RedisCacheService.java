package com.example.conditional.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache - created ONLY if cache.type=redis in application.properties
 *
 * @ConditionalOnProperty checks if a property exists and matches a value.
 */
@Service
@ConditionalOnProperty(name = "cache.type", havingValue = "redis")
public class RedisCacheService implements CacheService {

    private final Map<String, Object> cache = new HashMap<>();

    public RedisCacheService() {
        System.out.println("✓ RedisCacheService created (cache.type=redis)");
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
        System.out.println("Redis: Cached " + key);
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public String getCacheType() {
        return "Redis Cache (@ConditionalOnProperty)";
    }
}
