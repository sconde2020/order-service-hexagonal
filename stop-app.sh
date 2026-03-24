#!/bin/bash

echo "🚀 Arrêt de l'application..."

# Arrêter et supprimer les conteneurs existants (si nécessaire)
sudo docker compose -f docker-compose.yml down

# Vérifier si les conteneurs ont été arrêtés
if [ $? -eq 0 ]; then
    echo "✅ Application arrêtée avec succès."
else
    echo "❌ Une erreur est survenue lors de l'arrêt de l'application."
fi

# Supprimer les images et les volumes associés (optionnel)
sudo docker compose -f docker-compose.yml down --rmi all -v

# Vérifier si les images et les volumes ont été supprimés
if [ $? -eq 0 ]; then
    echo "✅ Images et volumes supprimés avec succès."
else
    echo "❌ Une erreur est survenue lors de la suppression des images et des volumes."
fi
