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

## Day 3 — Kafka Consumers and Docker Setup

Built inventory-service, payment-service, and notification-service.
Wired everything together with Docker Compose.

### What I implemented
- Kafka consumers using @KafkaListener in Spring Boot
- Each service consumes one topic and produces to the next
- notification-service listens to both payment.success and payment.failed
- Docker Compose runs Zookeeper, Kafka, Kafka-UI and all 4 services

### Key learnings
- @KafkaListener automatically deserializes JSON back into Java objects
- Consumer group IDs ensure each service processes events independently
- Kafka-UI at localhost:8080 lets you visually monitor all topics and messages
- Docker networks allow containers to communicate using service names

### Next step
Run docker-compose up and test the full event flow end to end.
```

---

## 🎯 Your Exact Order of Actions:
```
1. Generate inventory-service at start.spring.io → extract into folder
   → create all 7 files → commit each one separately

2. Generate payment-service at start.spring.io → extract into folder
   → create all 6 files → commit each one separately

3. Generate notification-service at start.spring.io → extract into folder
   → create all 4 files → commit each one separately

4. Create docker/docker-compose.yml → paste content → commit

5. Update learning-notes.md → commit

## Day 4 — Docker, CI/CD and Testing

Containerized all 4 services and ran the full event flow end to end.

### What I implemented
- Multi-stage Dockerfile for each service (build + run stages)
- Updated docker-compose with healthchecks and proper startup order
- GitHub Actions CI pipeline that builds all 4 services on every push
- Root .gitignore and CONTRIBUTING.md for project hygiene

### Key learnings
- Multi-stage Docker builds keep image size small by not including Maven in final image
- Healthchecks in docker-compose ensure Kafka is ready before services start
- KAFKA_ADVERTISED_LISTENERS needs two listeners: one for internal Docker network,
  one for external localhost access
- GitHub Actions runs in parallel for each service — much faster than sequential

### Full event flow tested
- POST /api/orders → order.created → inventory.reserved → payment.success → notification sent
- Watched all events flow through Kafka UI at localhost:8080
- Confirmed idempotency by sending same request twice

### Next step
Add Kubernetes manifests and deploy to local cluster with minikube.