#!/bin/bash

echo "🚀 Démarrage de l'application..."

# Démarrer l'application
sudo docker compose -f docker-compose.yml up -d

# Vérifier si les conteneurs ont démarré
if [ $? -eq 0 ]; then
    echo "✅ Application démarrée avec succès."
    echo "   -application: http://localhost:8080/orders"
    echo "   -application Swagger UI: http://localhost:8080/swagger-ui/index.html"
    echo "   -application actuator: http://localhost:8080/actuator/health"
    echo "   -prometheus: http://localhost:9090/targets"
    echo "   -grafana: http://localhost:3000 (login: admin, password: admin)"
else
    echo "❌ Une erreur est survenue lors du démarrage de l'application."
fi

# Attendre que l'application soit prête
echo "⏳ Attente de l'initialisation de l'application Spring Boot..."
until curl -s http://localhost:8080/actuator/health | grep -q '"status":"UP"'; do
    echo "   En attente..."
    sleep 2
done
echo "✅ Application prête!"

# Créer une commande en interceptant le statut HTTP
echo "📦 Création d'une commande de test..."

HTTP_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8080/orders \
     -H "Content-Type: application/json" \
     -d '{"product": "Test Product", "quantity": 2}')

# Extraire le body et le code HTTP
HTTP_BODY=$(echo "$HTTP_RESPONSE" | head -n -1)
HTTP_STATUS=$(echo "$HTTP_RESPONSE" | tail -n 1)

echo "   Réponse: $HTTP_BODY"
echo "   Status HTTP: $HTTP_STATUS"

if [ "$HTTP_STATUS" -eq 200 ] || [ "$HTTP_STATUS" -eq 201 ]; then
    echo "✅ Commande créée avec succès (HTTP $HTTP_STATUS)"
elif [ "$HTTP_STATUS" -eq 400 ]; then
    echo "❌ Erreur de validation (HTTP 400): $HTTP_BODY"
elif [ "$HTTP_STATUS" -eq 500 ]; then
    echo "❌ Erreur serveur (HTTP 500): $HTTP_BODY"
else
    echo "❌ Erreur inattendue (HTTP $HTTP_STATUS): $HTTP_BODY"
fi