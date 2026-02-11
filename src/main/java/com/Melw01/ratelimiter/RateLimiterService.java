package com.Melw01.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class RateLimiterService {
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;
    private final long refillIntervalMs;

    public RateLimiterService(int capacity, long refillIntervalMs) {
        this.capacity = capacity;
        this.refillIntervalMs = refillIntervalMs;
    }

    public boolean allowRequest(String clientId) {
        buckets.putIfAbsent(clientId, new TokenBucket(capacity, refillIntervalMs));
        return buckets.get(clientId).allowRequest();
    }
}