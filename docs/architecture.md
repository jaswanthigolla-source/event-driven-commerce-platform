🏗️ Production-Grade Event-Driven Microservices Architecture
1. Service Boundaries & Responsibilities
ServiceResponsibilityorder-serviceAccepts customer orders via REST API. Validates and persists orders. Publishes order.created event to Kafkainventory-serviceListens for order.created. Checks and reserves stock. Publishes inventory.reserved or inventory.failedpayment-serviceListens for inventory.reserved. Processes payment. Publishes payment.success or payment.failednotification-serviceListens for payment.success or payment.failed. Sends email/SMS to customer

2. Kafka Topics & Event Flow
Kafka Topics:
──────────────────────────────────────────
order.created
inventory.reserved
inventory.failed
payment.success
payment.failed
order.dlq              ← Dead Letter Queue
inventory.dlq
payment.dlq
Event Flow:
[Customer]
    │
    ▼ POST /orders
[order-service]
    │ publishes ──────────────────► order.created
                                        │
                                        ▼
                               [inventory-service]
                                        │
                          ┌─────────────┴──────────────┐
                          ▼                             ▼
                  inventory.reserved          inventory.failed
                          │                             │
                          ▼                             ▼
                 [payment-service]           [notification-service]
                          │                  (sends failure email)
                 ┌────────┴────────┐
                 ▼                 ▼
         payment.success    payment.failed
                 │                 │
                 ▼                 ▼
        [notification-service] (success or failure email)

3. Retry Strategy
Each service uses exponential backoff for retries:
Attempt 1 → immediate
Attempt 2 → wait 2 seconds
Attempt 3 → wait 4 seconds
Attempt 4 → wait 8 seconds
After 4 failures → send to DLQ
In Spring Boot with Kafka, configure this in application.yml:
yamlspring:
  kafka:
    consumer:
      max-poll-interval-ms: 300000
    listener:
      ack-mode: manual
      
# Retry config (use Spring-Retry or custom error handler)
retry:
  max-attempts: 4
  initial-interval: 2000
  multiplier: 2.0
```

---

## 4. Dead Letter Queue (DLQ) Handling

Every topic has a paired DLQ topic:
```
order.created     → failure → order.dlq
inventory.reserved → failure → inventory.dlq
payment.success   → failure → payment.dlq
How it works:

After max retries are exhausted, the message is published to the .dlq topic
A separate DLQ consumer (inside each service or a dedicated monitoring service) reads from DLQ topics
Operations team gets alerted
Messages can be replayed manually after fixing the bug

java// In your Kafka error handler
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
    DeadLetterPublishingRecoverer recoverer = 
        new DeadLetterPublishingRecoverer(template,
            (record, ex) -> new TopicPartition(record.topic() + ".dlq", -1));
    
    ExponentialBackOff backOff = new ExponentialBackOff(2000L, 2.0);
    backOff.setMaxAttempts(4);
    
    return new DefaultErrorHandler(recoverer, backOff);
}

5. Idempotency Approach
Problem: Kafka can deliver the same message more than once. We must not process it twice (e.g., charge payment twice).
Solution: Each event carries a unique eventId. Each service stores processed eventIds in a database table.
java// Event structure
{
  "eventId": "uuid-1234-5678",   // unique per event
  "orderId": "order-999",
  "timestamp": "2026-03-14T10:00:00Z",
  "payload": { ... }
}
java// Before processing, check:
if (processedEventRepository.existsByEventId(event.getEventId())) {
    log.info("Duplicate event ignored: {}", event.getEventId());
    return; // skip processing
}
// Process and then save the eventId
processedEventRepository.save(new ProcessedEvent(event.getEventId()));
Table in DB:
sqlCREATE TABLE processed_events (
    event_id VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMP DEFAULT NOW()
);
```

---

## 6. ASCII Architecture Diagram
```
┌─────────────────────────────────────────────────────────────┐
│                        KUBERNETES CLUSTER                    │
│                                                             │
│  ┌──────────────┐    order.created    ┌──────────────────┐  │
│  │ order-service│ ──────────────────► │inventory-service │  │
│  │  :8081       │                     │  :8082           │  │
│  └──────────────┘                     └────────┬─────────┘  │
│         ▲                                      │            │
│         │                          inventory.reserved       │
│    REST API                                    │            │
│         │                             ┌────────▼─────────┐  │
│    [Customer]                         │ payment-service  │  │
│                                       │  :8083           │  │
│                                       └────────┬─────────┘  │
│                                                │            │
│                                    payment.success/failed   │
│                                                │            │
│                                       ┌────────▼──────────┐ │
│                                       │notification-service│ │
│                                       │  :8084            │ │
│                                       └───────────────────┘ │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              APACHE KAFKA                            │   │
│  │  Topics: order.created │ inventory.reserved │        │   │
│  │          payment.success │ payment.failed │  *.dlq   │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐   │
│  │  PostgreSQL  │  │  Prometheus  │  │    Grafana     │   │
│  │  (each svc)  │  │  (metrics)   │  │  (dashboards)  │   │
│  └──────────────┘  └──────────────┘  └────────────────┘   │
└─────────────────────────────────────────────────────────────┘
