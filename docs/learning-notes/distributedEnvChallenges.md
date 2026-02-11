# Challenges of a Rate limiter in a Distributed Environment
1. Race condition
2. Synchronization issue

## Race Condition

### Challenge 
In a distributed environment, if multiple rate limiter instances (or even concurrent threads on a single instance) attempt to update the same counter in Redis using a simple read-increment-write sequence, a race condition can occur.

#### Example
1. Read: Imagine the current counter in Redis is X.
2. Request 1 (concurrently): Reads the counter value X.
3. Request 2 (concurrently): Also reads the counter value X before Request 1 has written its updated value.
4. Increment (Request 1): Increments its local copy of the counter (X + 1).
5. Increment (Request 2): Increments its local copy of the counter (X + 1).
6. Write (Request 1): Writes X + 1 back to Redis.
7. Write (Request 2): Writes X + 1 back to Redis, overwriting the value written by Request 1.

Consequence: Instead of the counter reflecting X + 2 (which would be the correct value after two increments), it only shows X + 1. This means the system has processed two requests but only accounted for one, effectively allowing more requests than the defined rate limit without detection.

### Solutions
#### 1. Lua Scripts 
Redis can execute Lua scripts atomically. This means that an entire script runs as a single, uninterruptible operation. You can encapsulate the read-check-increment logic within a Lua script. Redis will guarantee that no other client can modify the data during the script's execution. This ensures that the counter is read, incremented, and written back as a single, atomic transaction, preventing other concurrent requests from interfering.

Example: A script could read the current counter, check if incrementing it would exceed the limit, and if not, increment it and set an expiry, all in one go.

#### 2. Redis Sorted Sets (for Sliding Window Log) 
While not directly for a simple INCR counter, for algorithms like the Sliding Window Log that track timestamps, Redis sorted sets are incredibly useful. Sorted sets allow you to store timestamps as members with their actual timestamp as the score. Operations like adding new timestamps (ZADD), removing old ones (ZREMRANGEBYSCORE), and getting the current count (ZCARD) can be combined and executed efficiently. The atomic nature of these individual Redis commands (or combining them with transactions/Lua scripts) helps manage concurrency for timestamp-based rate limiting.

Example: When a request arrives, you can use ZREMRANGEBYSCORE to remove all timestamps older than the current window, then ZADD the new request's timestamp, and finally ZCARD to get the current count, potentially within a WATCH/MULTI/EXEC block or a Lua script for stronger atomicity if multiple steps are involved.


## Synchronization Issue
### Challenge
When clients can send requests to different rate limiter servers (due to stateless web tiers or load balancing), each server might only have a partial view of the client's total requests. Without synchronization, one rate limiter server wouldn't know about requests processed by another server for the same client, leading to inaccurate rate limiting.
### Solution
1. **Centralized data store like Redis**: By storing all rate limiting counters and related data (like timestamps for sliding window algorithms) in a single, shared Redis instance (or a Redis cluster), all distributed rate limiter servers can read and write to the same source of truth. This ensures that every rate limiter instance has an up-to-date and consistent view of a client's request history, allowing for accurate rate limiting across the entire distributed system.
2. **Sticky Sessions** (Not Recommended): not scalable or flexible