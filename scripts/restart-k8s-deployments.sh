#!/usr/bin/env bash
set -euo pipefail

# Restarts all microservices deployments in the "microservices" namespace
# Usage: ./restart-k8s-deployments.sh

kubectl rollout restart deployment/authentication-service -n microservices
kubectl rollout restart deployment/usermanagement-service -n microservices
kubectl rollout restart deployment/registry-service -n microservices
kubectl rollout restart deployment/admin-service -n microservices
kubectl rollout restart deployment/chess-service -n microservices
kubectl rollout restart deployment/fitness-service -n microservices
kubectl rollout restart deployment/music-service -n microservices
kubectl rollout restart deployment/gateway-service -n microservices
kubectl rollout restart deployment/website-service -n microservices
