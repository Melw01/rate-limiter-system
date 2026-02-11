# Rate Limiter Architecture

### Single-Instance Token Bucket

```mermaid
graph LR
    Client1 --> API[API Gateway]
    Client2 --> API
    API --> RateLimiter[RateLimiterService]
    RateLimiter --> TokenBucket[Token Bucket per client]
```

### Distributed Token Bucket
```mermaid
graph LR
    Client1 --> API1[API Instance 1]
    Client2 --> API2[API Instance 2]
    API1 --> Redis[Redis Token Store]
    API2 --> Redis
    Redis --> TokenBucket[Token Bucket per client]
```

### Components
1. **API Gateway**: Entry point for all requests. Routes to backend service.
2. **RateLimiterService**: Core logic that decides if a request is allowed.
3. **TokenBucket**: Stores tokens per client, refills periodically.
4. **Redis (Distributed)**: Shared state across multiple API instances.