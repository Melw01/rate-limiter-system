# Rate Limiting Algorithms
1. Token Bucket 
2. Leaking Bucket 
3. Fixed Window Counter
4. Sliding Window Log
5. Sliding Window Counter

## 1. Token Bucket
A bucket with a fixed capacity holds "tokens." Tokens are added to the bucket at a constant refill rate. Each incoming request consumes one token.
#### Mechanism
- If a request arrives and there are tokens available, it consumes a token and proceeds.
- If no tokens are available, the request is dropped (or queued, depending on implementation).
- The bucket cannot hold more tokens than its capacity; excess tokens "overflow."
#### Parameters
1. Bucket size (capacity)
2. Refill rate (tokens per second/minute). 

**Pros**: Easy to implement, memory efficient, allows for bursts of traffic (up to bucket size).

**Cons**: Tuning bucket size and refill rate can be challenging.

## 2. Leaking Bucket

Similar to a physical leaking bucket, requests are processed at a fixed output rate. It uses a FIFO queue to hold incoming requests.
#### Mechanism
- Requests arrive and are added to a queue if it's not full.
- If the queue is full, incoming requests are dropped.
- Requests are then pulled from the queue and processed at a constant, fixed "outflow rate," regardless of the incoming request rate.
#### Parameters
1. Bucket size (queue size)
2. Outflow rate (requests processed per second/minute).

**Pros**: Memory efficient (limited queue size), ensures a stable and smooth outflow rate, which can prevent server overload from bursts.

**Cons**: A burst of traffic can fill the queue with "old" requests, potentially delaying recent requests excessively or causing them to be dropped. Tuning parameters can be challenging.


## 3. Fixed Window Counter

#### Core Idea
Divides time into fixed-size windows (e.g., 1 minute). Each window has a counter.
#### Mechanism
- When a request arrives, the counter for the current window is incremented.
- If the counter exceeds a predefined threshold within that window, subsequent requests for that window are dropped.
- The counter resets at the start of each new window.

**Pros**: Memory efficient, easy to understand and implement, simple reset at window boundaries.

**Cons**: Major issue with "edge cases." A burst of traffic at the very end of one window combined with a burst at the very beginning of the next window can allow significantly more requests than the limit within a shorter period (e.g., almost double the limit in two consecutive seconds across the window boundary).


## 4. Sliding Window Log
Keeps a log of timestamps for every request made by a client.
#### Mechanism
- When a new request arrives, it removes all timestamps from the log that are older than the current time window (e.g., if the window is 1 minute, remove timestamps older than 1 minute ago).
- The new request's timestamp is added to the log.
- If the current number of timestamps in the log is less than or equal to the allowed limit, the request is accepted. Otherwise, it's rejected.

**Pros**: Very accurate rate limiting; it ensures that the rate limit is never exceeded within any rolling time window.

**Cons**: Consumes a lot of memory because it stores individual timestamps for every request, which can be significant for high traffic.


## 5. Sliding Window Counter
A hybrid approach combining fixed window counters with an approximation to smooth out the edge case issue.
#### Mechanism
- Maintains counters for the current fixed window and the previous fixed window.
- For an incoming request, the count for the current rolling window is calculated as: (requests in current window) + (requests in previous window * overlap percentage of rolling window and previous window).
- If this calculated count is below the limit, the request is allowed, and the current window's counter is incremented.

**Pros**: More memory efficient than Sliding Window Log, smooths out traffic spikes better than Fixed Window Counter (due to the weighted average), more accurate than Fixed Window Counter without the high memory cost of Sliding Window Log.

**Cons**: It's an approximation of the true rate (assumes requests in the previous window are evenly distributed), so it's not perfectly accurate, but often good enough for practical purposes.