# Order Service - Hexagonal Architecture & DDD Demo

A demo application showcasing **Hexagonal Architecture** (Ports & Adapters) combined with **Domain-Driven Design (DDD)** principles using Spring Boot.

## 🏗️ Architecture Overview

```
                              HTTP Requests
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           ADAPTERS (IN)                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  OrderController                                                     │    │
│  │  ├── dto/ (CreateOrderRequest, CreateOrderResponse, GetOrderResponse)│    │
│  │  └── mapper/ (OrderDtoMapper)                                        │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           PORTS (IN)                                         │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐  │
│  │ CreateOrderUseCase  │  │  GetOrderUseCase    │  │ GetOrderListUseCase │  │
│  └─────────────────────┘  └─────────────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         APPLICATION LAYER                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  Services: CreateOrderService, GetOrderService, GetOrderListService  │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  Commands: CreateOrderCommand                                        │    │
│  │  Results: CreateOrderResult, GetOrderResult, GetOrderListResult      │    │
│  │  Exceptions: OrderNotFoundException                                  │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           DOMAIN LAYER                                       │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐  │
│  │   Order (Entity)    │  │ OrderDomainService  │  │ OrderValidation     │  │
│  │                     │  │                     │  │ Exception           │  │
│  └─────────────────────┘  └─────────────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           PORTS (OUT)                                        │
│  ┌───────────────────────────────┐  ┌───────────────────────────────────┐   │
│  │     OrderRepositoryPort       │  │    OrderEventPublisherPort        │   │
│  └───────────────────────────────┘  └───────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           ADAPTERS (OUT)                                     │
│  ┌───────────────────────────────┐  ┌───────────────────────────────────┐   │
│  │  JpaOrderRepositoryAdapter    │  │  KafkaOrderPublisherAdapter       │   │
│  │  ├── entity/OrderEntity       │  │  └── OrderEvent                   │   │
│  │  ├── mapper/OrderEntityMapper │  │                                   │   │
│  │  └── SpringDataOrderRepository│  │                                   │   │
│  └───────────────────────────────┘  └───────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              INFRASTRUCTURE                                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │  PostgreSQL │  │    Kafka    │  │ Prometheus  │  │      Grafana        │ │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 📁 Project Structure

```
src/main/java/com/example/order/
├── OrderServiceHexagonalApplication.java
├── adapters/
│   ├── in/
│   │   └── web/                          # REST Controllers (Driving Adapters)
│   │       ├── OrderController.java
│   │       ├── dto/
│   │       │   ├── CreateOrderRequest.java
│   │       │   ├── CreateOrderResponse.java
│   │       │   ├── GetOrderListResponse.java
│   │       │   └── GetOrderResponse.java
│   │       └── mapper/
│   │           └── OrderDtoMapper.java
│   └── out/
│       ├── persistence/                  # JPA Repository (Driven Adapter)
│       │   ├── JpaOrderRepositoryAdapter.java
│       │   ├── SpringDataOrderRepository.java
│       │   ├── entity/
│       │   │   └── OrderEntity.java
│       │   └── mapper/
│       │       └── OrderEntityMapper.java
│       └── messaging/                    # Kafka Publisher (Driven Adapter)
│           ├── KafkaOrderPublisherAdapter.java
│           └── OrderEvent.java
├── application/
│   ├── command/
│   │   └── CreateOrderCommand.java
│   ├── exception/
│   │   └── OrderNotFoundException.java
│   ├── port/
│   │   ├── in/                           # Input Ports (Use Cases)
│   │   │   ├── CreateOrderUseCase.java
│   │   │   ├── GetOrderListUseCase.java
│   │   │   └── GetOrderUseCase.java
│   │   └── out/                          # Output Ports (Repositories, Publishers)
│   │       ├── OrderRepositoryPort.java
│   │       └── OrderEventPublisherPort.java
│   ├── result/
│   │   ├── CreateOrderResult.java
│   │   ├── GetOrderListResult.java
│   │   └── GetOrderResult.java
│   └── service/
│       ├── CreateOrderService.java
│       ├── GetOrderListService.java
│       └── GetOrderService.java
├── domain/
│   ├── exception/
│   │   └── OrderValidationException.java
│   ├── model/
│   │   └── Order.java
│   └── service/
│       └── OrderDomainService.java
└── infrastructure/
    ├── aspect/
    │   └── ControllerAspect.java
    ├── config/
    │   └── DomainConfig.java
    ├── exception/
    │   ├── ApiError.java
    │   └── GlobalExceptionsHandler.java
    ├── health/
    │   ├── KafkaAdminClientConfig.java
    │   └── KafkaHealthIndicator.java
    └── openapi/
        └── OpenApiConfig.java
```

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 21** | Programming Language |
| **Spring Boot 4.x** | Application Framework |
| **PostgreSQL 16** | Relational Database |
| **Apache Kafka** | Event Streaming (KRaft mode) |
| **Prometheus** | Metrics Collection |
| **Grafana** | Metrics Visualization |
| **Docker & Docker Compose** | Containerization |
| **Spring Data JPA** | Data Access |
| **Hibernate** | ORM |
| **Swagger/OpenAPI** | API Documentation |

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Java 21 (for local development)
- Maven (for local development)

### Run with Docker Compose

```bash
# Start all services
./start-app.sh

# Or manually
docker compose up -d --build
```

### Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| **Application** | http://localhost:8080 | - |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | - |
| **Actuator Health** | http://localhost:8080/actuator/health | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin / admin |
| **PostgreSQL** | localhost:5437 | postgres / postgres |
| **Kafka** | localhost:9096 | - |

## 📡 API Endpoints

### Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/orders` | Create a new order |
| `GET` | `/orders/{id}` | Get order by ID |
| `GET` | `/orders` | Get all orders |

### Example Requests

```bash
# Create an order
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"product": "Laptop", "quantity": 2}'

# Get order by ID
curl http://localhost:8080/orders/1

# Get all orders
curl http://localhost:8080/orders
```

## 📊 Monitoring

### Prometheus Metrics

Access Prometheus at http://localhost:9090

Useful queries:
```promql
# Request count by endpoint
sum by(uri, method) (http_server_requests_seconds_count)

# Request rate per second
rate(http_server_requests_seconds_count[1m])

# Average response time
rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])

# 95th percentile response time
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

### Grafana Dashboards

1. Access Grafana at http://localhost:3000 (admin/admin)
2. Add Prometheus data source: `http://prometheus:9090`
3. Import dashboard ID **4701** (Spring Boot Statistics) or **11378** (JVM Micrometer)

## 📨 Kafka Events

When an order is created, an `OrderEvent` is published to the `orders` topic.

### Verify Kafka Messages

```bash
# List topics
docker exec kafka /opt/kafka/bin/kafka-topics.sh \
  --list --bootstrap-server localhost:9096

# Consume messages
docker exec kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9096 \
  --topic orders \
  --from-beginning
```

## 🧪 Local Development

### Run PostgreSQL & Kafka locally

```bash
# Start only infrastructure
docker compose up -d postgres kafka
```

### Run the application

```bash
./mvnw spring-boot:run
```

### Configuration

The application uses different configurations:
- **Local**: `application.yaml` (localhost:5432, localhost:9092)
- **Docker**: Environment variables override (postgres:5437, kafka:9096)

## 🏛️ Hexagonal Architecture Principles

### Ports
- **Input Ports** (Use Cases): Define what the application can do
- **Output Ports**: Define what the application needs from external systems

### Adapters
- **Driving Adapters** (Primary): REST Controllers, CLI, etc.
- **Driven Adapters** (Secondary): Database, Message Queue, External APIs

### Benefits
- ✅ **Testability**: Easy to mock ports for unit testing
- ✅ **Flexibility**: Swap adapters without changing business logic
- ✅ **Independence**: Domain logic is isolated from infrastructure
- ✅ **Maintainability**: Clear separation of concerns

## 📝 DDD Concepts Used

| Concept | Implementation |
|---------|----------------|
| **Entity** | `Order` - has identity and lifecycle |
| **Domain Service** | `OrderDomainService` - business logic |
| **Domain Exception** | `OrderValidationException` |
| **Application Service** | `CreateOrderService`, `GetOrderService` |
| **Repository Port** | `OrderRepositoryPort` |

## 🛑 Stop Services

```bash
# Stop all containers
./stop-app.sh

# Stop and remove volumes (clean data)
./stop-app.sh --clean
```

## 📄 License

This project is for educational purposes - demonstrating Hexagonal Architecture and DDD patterns.