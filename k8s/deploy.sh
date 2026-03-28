#!/bin/bash

# =============================================================================
# Order Service Hexagonal - Kubernetes Deployment Script
# =============================================================================

set -e

NAMESPACE="order-service-hexagonal-ns"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Par défaut, ne pas déployer ELK ni monitoring
DEPLOY_ELK=false
DEPLOY_MONITORING=false

# Parsing des options
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --elk|-e) DEPLOY_ELK=true ;;
        --monitoring|-m) DEPLOY_MONITORING=true ;;
        *) echo "Option inconnue: $1" ; exit 1 ;;
    esac
    shift
done

echo "=============================================="
echo "  Order Service Hexagonal - K8s Deployment"
echo "=============================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl is not installed. Please install it first."
    exit 1
fi

# Check if cluster is accessible
if ! kubectl cluster-info &> /dev/null; then
    print_error "Cannot connect to Kubernetes cluster. Please check your configuration."
    exit 1
fi

print_status "Connected to Kubernetes cluster"

# Build the Docker image if Dockerfile exists
if [ -f "$SCRIPT_DIR/../Dockerfile" ]; then
    print_warning "Building Docker image for order-service-hexagonal..."
    
    # Check if using minikube
    if command -v minikube &> /dev/null && minikube status &> /dev/null; then
        eval $(minikube -p minikube docker-env)
        print_status "Using Minikube's Docker daemon"
    fi
    
    docker build -t order-service-hexagonal:latest "$SCRIPT_DIR/.."
    print_status "Docker image built successfully"
fi

# 1. Create Namespace
echo ""
echo "Step 1: Creating namespace..."
kubectl apply -f "$SCRIPT_DIR/namespace.yaml"
print_status "Namespace '$NAMESPACE' created"

# 2. Deploy Infrastructure (PostgreSQL + Kafka)
echo ""
echo "Step 2: Deploying infrastructure..."

echo "  - Deploying PostgreSQL..."
kubectl apply -k "$SCRIPT_DIR/infrastructure/postgres/"
print_status "PostgreSQL deployed"

echo "  - Deploying Kafka..."
kubectl apply -k "$SCRIPT_DIR/infrastructure/kafka/"
print_status "Kafka deployed"

# Wait for infrastructure to be ready
echo ""
echo "Waiting for infrastructure to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres -n $NAMESPACE --timeout=120s || true
kubectl wait --for=condition=ready pod -l app=kafka -n $NAMESPACE --timeout=180s || true
print_status "Infrastructure is ready"

if [ "$DEPLOY_ELK" = true ]; then
    echo ""
    echo "Step 3: Deploying ELK Stack..."

    echo "  - Deploying Elasticsearch..."
    kubectl apply -k "$SCRIPT_DIR/elk/elasticsearch/"
    print_status "Elasticsearch deployed"

    echo "  - Waiting for Elasticsearch..."
    kubectl wait --for=condition=ready pod -l app=elasticsearch -n $NAMESPACE --timeout=180s || true

    echo "  - Deploying Logstash..."
    kubectl apply -k "$SCRIPT_DIR/elk/logstash/"
    print_status "Logstash deployed"

    echo "  - Deploying Kibana..."
    kubectl apply -k "$SCRIPT_DIR/elk/kibana/"
    print_status "Kibana deployed"

    echo "  - Waiting for Logstash..."
    kubectl wait --for=condition=ready pod -l app=logstash -n $NAMESPACE --timeout=180s || true
fi

if [ "$DEPLOY_MONITORING" = true ]; then
    echo ""
    echo "Step 4: Deploying Monitoring Stack..."

    echo "  - Deploying Prometheus..."
    kubectl apply -k "$SCRIPT_DIR/monitoring/prometheus/"
    print_status "Prometheus deployed"

    echo "  - Deploying Grafana..."
    kubectl apply -k "$SCRIPT_DIR/monitoring/grafana/"
    print_status "Grafana deployed"
fi

# 5. Deploy Application
echo ""
echo "Step 5: Deploying Order Service Application..."

kubectl apply -k "$SCRIPT_DIR/app/"
print_status "Order Service Application deployed"

# Wait for application to be ready
echo ""
echo "Waiting for application to be ready..."
kubectl wait --for=condition=ready pod -l app=order-service -n $NAMESPACE --timeout=180s || true

# 6. Display deployment status
echo ""
echo "=============================================="
echo "  Deployment Complete!"
echo "=============================================="

echo ""
echo "Pods status:"
kubectl get pods -n $NAMESPACE

echo ""
echo "Services:"
kubectl get svc -n $NAMESPACE

echo ""
echo "=============================================="
echo "  Access URLs (NodePort)"
echo "=============================================="

# Get node IP
NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}' 2>/dev/null || echo "localhost")

# For Minikube
if command -v minikube &> /dev/null && minikube status &> /dev/null; then
    NODE_IP=$(minikube ip)
fi

echo ""
echo "  Order Service API : http://$NODE_IP:30080/orders"
if [ "$DEPLOY_ELK" = true ]; then
    echo "  Elasticsearch      : http://$NODE_IP:9200"
    echo "  Logstash          : http://$NODE_IP:5044"
    echo "  Kibana            : http://$NODE_IP:30561"
fi
if [ "$DEPLOY_MONITORING" = true ]; then
    echo "  Prometheus        : http://$NODE_IP:30090"
    echo "  Grafana           : http://$NODE_IP:30030 (admin/admin)"
fi
echo ""
echo "=============================================="
