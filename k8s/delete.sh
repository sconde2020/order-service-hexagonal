#!/bin/bash

# =============================================================================
# Order Service Hexagonal - Kubernetes Delete Script
# =============================================================================

set -e

NAMESPACE="order-service-hexagonal-ns"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=============================================="
echo "  Order Service Hexagonal - K8s Cleanup"
echo "=============================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

# Confirmation prompt
read -p "Are you sure you want to delete all resources in namespace '$NAMESPACE'? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Aborted."
    exit 0
fi

# Check if namespace exists
if ! kubectl get namespace $NAMESPACE &> /dev/null; then
    print_warning "Namespace '$NAMESPACE' does not exist. Nothing to delete."
    exit 0
fi

echo ""
echo "Deleting resources..."

# 1. Delete Application
echo "  - Deleting Order Service Application..."
kubectl delete -k "$SCRIPT_DIR/app/" --ignore-not-found=true
print_status "Application deleted"

## 2. Delete Monitoring Stack
#echo "  - Deleting Grafana..."
#kubectl delete -k "$SCRIPT_DIR/monitoring/grafana/" --ignore-not-found=true
#print_status "Grafana deleted"
#
#echo "  - Deleting Prometheus..."
#kubectl delete -k "$SCRIPT_DIR/monitoring/prometheus/" --ignore-not-found=true
#print_status "Prometheus deleted"

# 3. Delete ELK Stack
echo "  - Deleting Kibana..."
kubectl delete -k "$SCRIPT_DIR/elk/kibana/" --ignore-not-found=true
print_status "Kibana deleted"

echo "  - Deleting Logstash..."
kubectl delete -k "$SCRIPT_DIR/elk/logstash/" --ignore-not-found=true
print_status "Logstash deleted"

echo "  - Deleting Elasticsearch..."
kubectl delete -k "$SCRIPT_DIR/elk/elasticsearch/" --ignore-not-found=true
print_status "Elasticsearch deleted"

# 4. Delete Infrastructure
echo "  - Deleting Kafka..."
kubectl delete -k "$SCRIPT_DIR/infrastructure/kafka/" --ignore-not-found=true
print_status "Kafka deleted"

echo "  - Deleting PostgreSQL..."
kubectl delete -k "$SCRIPT_DIR/infrastructure/postgres/" --ignore-not-found=true
print_status "PostgreSQL deleted"

# 5. Delete PVCs (optional - data will be lost!)
echo ""
read -p "Do you want to delete PersistentVolumeClaims (all data will be lost)? (y/N) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "  - Deleting PVCs..."
    kubectl delete pvc --all -n $NAMESPACE --ignore-not-found=true
    print_status "PVCs deleted"
fi

# 6. Delete Namespace
echo ""
read -p "Do you want to delete the namespace '$NAMESPACE'? (y/N) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "  - Deleting namespace..."
    kubectl delete -f "$SCRIPT_DIR/namespace.yaml" --ignore-not-found=true
    print_status "Namespace deleted"
fi

echo ""
echo "=============================================="
echo "  Cleanup Complete!"
echo "=============================================="
