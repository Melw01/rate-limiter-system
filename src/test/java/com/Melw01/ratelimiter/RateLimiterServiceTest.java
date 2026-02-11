package com.Melw01.ratelimiter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServiceTest {

    private static final String USER1 = "user1";

    @Test
    void shouldAllowRequestsWithinCapacity() {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(3, 1);

        assertTrue(limiter.allowRequest(USER1));
        assertTrue(limiter.allowRequest(USER1));
        assertTrue(limiter.allowRequest(USER1));
    }

    @Test
    void shouldBlockWhenCapacityExceeded() {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(2, 1);

        assertTrue(limiter.allowRequest(USER1));
        assertTrue(limiter.allowRequest(USER1));
        assertFalse(limiter.allowRequest(USER1)); // Exceeds capacity
    }

    @Test
    void shouldRefillAfterTimePasses() throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(1, 1);

        assertTrue(limiter.allowRequest(USER1));
        assertFalse(limiter.allowRequest(USER1));

        Thread.sleep(1000); // wait 1 second for refill

        assertTrue(limiter.allowRequest(USER1));
    }
}
