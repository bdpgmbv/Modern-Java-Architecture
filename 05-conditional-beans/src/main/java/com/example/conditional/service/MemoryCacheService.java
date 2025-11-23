package com.example.conditional.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache - created ONLY if cache.type=memory OR cache.type is missing.
 *
 * matchIfMissing = true means: "Create this bean if property doesn't exist"
 */
@Service
@ConditionalOnProperty(name = "cache.type", havingValue = "memory", matchIfMissing = true)
public class MemoryCacheService implements CacheService {

    private final Map<String, Object> cache = new HashMap<>();

    public MemoryCacheService() {
        System.out.println("✓ MemoryCacheService created (cache.type=memory or missing)");
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
        System.out.println("Memory: Cached " + key);
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public String getCacheType() {
        return "Memory Cache (@ConditionalOnProperty with matchIfMissing)";
    }
}
