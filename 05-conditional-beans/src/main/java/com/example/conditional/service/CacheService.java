package com.example.conditional.service;

public interface CacheService {
    void put(String key, Object value);
    Object get(String key);
    String getCacheType();
}
