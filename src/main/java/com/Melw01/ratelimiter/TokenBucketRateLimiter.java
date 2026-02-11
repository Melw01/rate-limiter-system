package com.Melw01.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token Bucket Rate Limiter (In-Memory Implementation)
 * <p>
 * This implementation:
 * - Maintains a separate bucket per key (e.g., user ID, API key, IP)
 * - Refills tokens at a fixed rate per second
 * - Allows bursts up to bucket capacity
 * <p>
 * NOTE:
 * This version uses second-level precision and is intended
 * for learning purposes. In production systems, higher
 * precision timing and distributed storage (e.g., Redis)
 * would typically be required.
 */
public class TokenBucketRateLimiter implements RateLimiter {

    private final long capacity;                // Max number of tokens a bucket can hold
    private final long refillTokensPerSecond;   // Number of tokens added per second
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();  // Stores a bucket per key (thread-safe map)

    public TokenBucketRateLimiter(long capacity, long refillTokensPerSecond) {
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
    }

    /**
     * Determines whether a request for a given key is allowed.
     *
     * @param key Identifier for rate limiting (user ID, API key, etc.)
     * @return true if request is allowed, false if rate limit exceeded
     */
    @Override
    public boolean allowRequest(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(capacity, refillTokensPerSecond));
        return bucket.tryConsume();
    }


}
