echo "Removing existing Prometheus container (if any)..."
docker rm -f prometheus 2>/dev/null || true

echo "Starting Prometheus container..."
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  --add-host=host.docker.internal:host-gateway \
  -v "$PWD/prometheus.yml:/etc/prometheus/prometheus.yml:ro" \
  prom/prometheus