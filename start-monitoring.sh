#!/bin/bash

echo "🚀 Démarrage du monitoring..."

# Arrêter et supprimer les conteneurs existants (si nécessaire)
sudo docker compose -f docker-compose-monitoring.yml down

# Démarrer Prometheus et Grafana
sudo docker compose -f docker-compose-monitoring.yml up -d

echo "✅ Services démarrés :"
echo "   - Prometheus : http://localhost:9090"
echo "   - Grafana    : http://localhost:3000 (admin/admin)"
echo ""
echo "📊 N'oubliez pas de démarrer votre application Spring Boot sur le port 8080"
echo "   pour que Prometheus puisse collecter les métriques."
echo "   Vous pouvez accéder à votre application ici : http://localhost:8080/swagger-ui.html"