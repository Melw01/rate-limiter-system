package com.Melw01.ratelimiter;

public interface RateLimiter {
    boolean allowRequest(String key);
}