# Contributing

## Local Development Setup

### Prerequisites
- Java 17+
- Docker Desktop
- Maven 3.9+
- Git

### Run Locally

1. Clone the repo
```bash
git clone git@github.com:jaswanthigolla-source/event-driven-commerce-platform.git
cd event-driven-commerce-platform
```

2. Start all services
```bash
cd docker
docker-compose up --build
```

3. Access services
- order-service API: http://localhost:8081
- Kafka UI: http://localhost:8080

### Test the Event Flow

Send a test order:
```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-1",
    "productId": "product-101",
    "quantity": 2,
    "totalAmount": 99.99
  }'
```

Watch events flow through Kafka UI at http://localhost:8080

## Commit Message Format
```
feat: add new feature
fix: fix a bug
config: configuration changes
docs: documentation updates
docker: docker related changes
ci: CI/CD changes
notes: learning notes updates
```