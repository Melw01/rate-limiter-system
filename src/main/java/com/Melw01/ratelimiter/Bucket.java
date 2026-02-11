package com.Melw01.ratelimiter;
import java.time.Instant;

/**
 * Represents a single token bucket.
 * <p>
 * Package-private to restrict usage within rate limiter package.
 * Thread-safe at bucket level using synchronized methods.
 */
class Bucket {

    private final long capacity;
    private final long refillTokensPerSecond;

    private long tokens;
    private long lastRefillTimestamp;

    Bucket(long capacity, long refillTokensPerSecond) {
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
        this.tokens = capacity; // Start full to allow burst
        this.lastRefillTimestamp = Instant.now().getEpochSecond();
    }

    /**
     * Attempts to consume one token.
     *
     * @return true if request is allowed, false if rate limit exceeded
     */
    synchronized boolean tryConsume() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    /**
     * Refills tokens based on elapsed time.
     * Uses second-level precision.
     */
    private void refill() {
        long now = Instant.now().getEpochSecond();
        long secondsSinceLastRefill = now - lastRefillTimestamp;

        if (secondsSinceLastRefill > 0) {
            long tokensToAdd = secondsSinceLastRefill * refillTokensPerSecond;

            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }
}