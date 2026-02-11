package com.Melw01.ratelimiter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate-limit")
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    public RateLimiterController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping
    public ResponseEntity<String> checkRateLimit(@RequestParam String key) {

        boolean allowed = rateLimiterService.isAllowed(key);

        if (allowed) {
            return ResponseEntity.ok("Request allowed");
        }

        return ResponseEntity.status(429)
                .body("Too Many Requests - Rate limit exceeded");
    }
}
