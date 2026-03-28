# Order Service - Hexagonal Architecture & DDD Demo

A demo application showcasing **Hexagonal Architecture** (Ports & Adapters) combined with **Domain-Driven Design (DDD)** principles using Spring Boot.

The demo includes an order management system with : 

- REST API for creating and retrieving orders
- PostgreSQL for data persistence
- Apache Kafka for event streaming
- Prometheus & Grafana for monitoring
- ELK Stack for logging
- Docker for containerization
- Kubernetes (Minikube) for orchestration


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

## Configuration

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


## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 21** | Programming Language |
| **Spring Boot 4.x** | Application Framework |
| **PostgreSQL 16** | Relational Database |
| **Apache Kafka** | Event Streaming (KRaft mode) |
| **Prometheus** | Metrics Collection |
| **Grafana** | Metrics Visualization |
| **ELK Stack** | Logging |
| **Docker & Docker Compose** | Containerization |
| **Kubernetes (Minikube)** | Container Orchestration | 
| **Spring Data JPA** | Data Access |
| **Hibernate** | ORM |
| **Swagger/OpenAPI** | API Documentation |

## 🚀 Quick Start

### Prerequisites

- Java 21 (for local development)
- Maven (for local development)
- PostgreSQL (if running locally without Docker)
- Apache Kafka (if running locally without Docker)
- Docker & Docker Compose (for containerized deployment)
- Kubernetes (optional, for orchestration)


### Run locally (without Docker)
For PostgreSQL, ensure it's running on localhost:5432 with user 'postgres' and password 'postgres'
For Kafka, ensure it's running on localhost:9092

```bash
./mvnw spring-boot:run
```

### Run with Docker Compose

#### Start all services (application + infrastructure + monitoring + logging)

```bash
cd docker
./start-app.sh
```

#### Stop all services (application + infrastructure + monitoring + logging)
```bash
cd docker
./stop-app.sh
```

### Run with Kubernetes (Minikube)

#### Deploy application and infrastructure to Minikube
```bash
# Start Minikube
minikube start 

# Apply Kubernetes manifests
cd k8s
.deploy.sh
```

#### Deploy application, infrastructure, monitoring, and logging to Minikube
```bash
# Start Minikube
minikube start 

# Apply Kubernetes manifests
cd k8s
.deploy.sh --elk --monitoring
```

#### Stop application and infrastructure in Minikube
```bash
cd k8s
.delete.sh
```

### Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| **Application** | http://localhost:8080 | - |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | - |
| **Actuator Health** | http://localhost:8080/actuator/health | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin / admin |
| **ELK Stack** | http://localhost:5601 (Kibana) | - |
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

## 📝 Logging

L'application utilise la **stack ELK** (Elasticsearch, Logstash, Kibana) pour la gestion centralisée des logs.

### Accès à Kibana

- URL : http://localhost:5601
- Aucun identifiant requis par défaut

### Fichiers de configuration

- **Logback** : `src/main/resources/logback-spring.xml`
- **Logstash** : `docker/logstash/pipeline/logstash.conf`
- **Kibana** : `k8s/elk/kibana/deployment.yaml`

### Démarrage de la stack ELK

- Avec Docker Compose :
  ```bash
  cd docker
  ./start-app.sh
  ```
- Avec Kubernetes (Minikube) :
  ```bash
  cd k8s
  ./deploy.sh --elk
  ```

### Visualisation des logs

1. Accédez à Kibana : http://localhost:5601
2. Créez un index pattern (ex : `logstash-*`)
3. Visualisez les logs de l'application dans Discover


