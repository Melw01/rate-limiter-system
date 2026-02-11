# Rate Limiter System Overview

**Goal:** Protect APIs from overuse while allowing bursts and fair usage across multiple clients.

**Key Decisions:**
- Token Bucket algorithm
- Thread-safe in-memory implementation
- Optional distributed deployment with Redis

**Requirements:**
- Max 100 requests/sec per client
- Handle concurrent requests safely

### Scenario 1: No Rate Limiter, Normal Load
```mermaid
sequenceDiagram
    User/Third Party App->>API Server: sends API request
    API Server->>User/Third Party App:Return 200 OK / Data
```

### Scenario 2: No Rate Limiter, Accidental Overload / Malicious Attacks (DoS)
```mermaid
sequenceDiagram
    User/Third Party App->>API Server: sends 100k API requests
    Note right of API Server: Overload
    API Server--xUser/Third Party App: sends 429 (too many reqs)
```

### Scenario 3: Rate Limiter, Accidental Overload / Malicious Attacks (DoS)
```mermaid
sequenceDiagram
    participant User
    box Navy API Gateway 1
        participant Rate Limiter 1
    end
    participant Redis Cache
    participant API Server
%% Request flow
    User ->> Rate Limiter 1: sends request
    Note over Rate Limiter 1: Algorithm: Token Bucket
    Rate Limiter 1 ->> Rate Limiter 1: lookup rate limit rules in local cache
alt cache hit
Rate Limiter 1 ->> Rate Limiter 1: load rules from local in-mem cache
Note over Rate Limiter 1: LOCAL RULE CACHE
    else cache miss / first load
Rate Limiter 1 ->> Rate Limiter 1: load rules from disk (YAML/JSON) and stored in distributed cache
    Note over Rate Limiter 1: DISTRIBUTED RULE CACHE (shared by RLs)
    Rate Limiter 1 ->> Rate Limiter 1: loads rules from distributed cache to local cache
end
%% Check token count in Redis
Rate Limiter 1 ->> Redis: check/update token counters
alt has token
Rate Limiter 1 ->> API Server: send request to API
API Server -->> User: 200 OK / Data
else no token
Rate Limiter 1->> Rate Limiter 1: request is dropped
Rate Limiter 1-->> User: 429 Too Many Requests
Note over Rate Limiter 1, User: X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Retry-After
end
```
- X-Ratelimit-Limit: Indicates the maximum number of calls the client can make per time window.
- X-Ratelimit-Remaining: Shows the remaining number of allowed requests within the current window.
- X-Ratelimit-Retry-After: Specifies the number of seconds to wait until the client can make another request without being throttled.