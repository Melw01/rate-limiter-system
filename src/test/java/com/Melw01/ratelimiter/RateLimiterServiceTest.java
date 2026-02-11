package com.Melw01.ratelimiter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RateLimiterServiceTest {

    @Test
    public void testSingleUserLimit() {
        RateLimiterService rl = new RateLimiterService(2, 1000);
        String clientId = "user1";

        assertTrue(rl.allowRequest(clientId));
        assertTrue(rl.allowRequest(clientId));
        assertFalse(rl.allowRequest(clientId));
    }
}