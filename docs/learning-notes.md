# Learning Notes — Event-Driven Commerce Platform

---

## Day 1 — Project Setup and Architecture Design

Today I set up the full project structure for an event-driven microservices
platform using Spring Boot and Kafka.

### Key design decisions
- Split the system into 4 services: order, inventory, payment, notification
- Services communicate via Kafka topics instead of direct REST calls
- This decouples services completely — each can scale and fail independently
- Dead Letter Queues handle events that fail after 4 retries

### What I learned
- Event-driven architecture makes systems resilient — if a service goes down,
  events stay in Kafka and are processed when it recovers
- Kafka topics are a durable log — consumers can replay events from any point
- Idempotency is critical — Kafka delivers at-least-once, so each service
  must detect and skip duplicate events using a unique eventId

### Questions to explore next
- How Kafka consumer groups work when scaling horizontally
- How to implement idempotency keys cleanly in Spring Boot
- Best approach for DLQ monitoring and alerting in production

### Next step
Build the order-service Spring Boot skeleton with REST endpoint and Kafka producer.

## Day 2 — order-service Kafka Producer

Built the order-service with a full REST API and Kafka producer.

### What I implemented
- Order entity with JPA and H2 database
- OrderEvent model for Kafka messaging
- KafkaTemplate-based producer publishing to order.created topic
- REST endpoint POST /api/orders to accept customer orders
- Transactional flow: save order to DB then publish event

### Key learning
- KafkaTemplate.send() is non-blocking — the event is published async
- Used @Transactional to ensure DB save and event publish happen together
- Idempotency keys (eventId as UUID) generated per event to prevent duplicate processing

### Next step
Build inventory-service to consume order.created events.