# Rate Limiter System Overview

**Goal:** Protect APIs from overuse while allowing bursts and fair usage across multiple clients.

**Key Decisions:**
- Token Bucket algorithm
- Thread-safe in-memory implementation
- Optional distributed deployment with Redis

**Requirements:**
- Max 100 requests/sec per client
- Handle concurrent requests safely

