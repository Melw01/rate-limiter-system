package com.Melw01.ratelimiter;

import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private final RateLimiter rateLimiter;

    public RateLimiterService() {
        // capacity = 5 requests
        // refill = 2 tokens per second
        this.rateLimiter = new TokenBucketRateLimiter(5, 2);
    }

    public boolean isAllowed(String key) {
        return rateLimiter.allowRequest(key);
    }
}
