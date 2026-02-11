package com.Melw01.ratelimiter;

public class TokenBucket {
    private final int capacity;
    private int tokens;
    private final long refillIntervalMs;
    private long lastRefillTimestamp;

    public TokenBucket(int capacity, long refillIntervalMs) {
        this.capacity = capacity;
        this.tokens = capacity;
        this.refillIntervalMs = refillIntervalMs;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long intervals = (now - lastRefillTimestamp) / refillIntervalMs;
        if (intervals > 0) {
            tokens = Math.min(capacity, tokens + (int) intervals);
            lastRefillTimestamp += intervals * refillIntervalMs;
        }
    }
}
