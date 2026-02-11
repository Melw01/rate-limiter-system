# Performance Optimization

Two ways to improve performance optimization:
1. Multi-Data Center Setup
2. Eventual Consistency Model

## Multi-Data Center Setup
By deploying rate limiters across multiple geographically distributed data centers (or edge server locations), traffic can be routed to the closest server. This significantly reduces latency for users, improving the overall responsiveness of the system.
## Eventual Consistency Model
#### Definition
Instead of requiring all data centers to be perfectly synchronized immediately after every write (strong consistency), eventual consistency allows for a _slight delay_. Updates made in one data center will eventually propagate to all other data centers, but not necessarily instantaneously.
#### Performance Benefit
This relaxed consistency model significantly reduces the overhead and latency associated with coordinating writes and reads across geographically distributed data centers. It avoids the need for synchronous updates, which can be very slow over wide area networks.
#### Implication
For a short period, different rate limiter instances might have slightly different views of the counter for a given client. However, for many rate limiting scenarios, this minor and temporary inconsistency is an acceptable trade-off for the massive performance gains.