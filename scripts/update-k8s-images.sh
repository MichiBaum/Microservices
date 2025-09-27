#!/usr/bin/env bash
set -euo pipefail

# Usage: ./update-k8s-images.sh <version>
# Example: ./update-k8s-images.sh 1.1.0

VERSION="${1:-}"
if [[ -z "$VERSION" ]]; then
  echo "Error: version argument is required."
  echo "Usage: $0 <version>"
  exit 1
fi

SERVICES=(
  "authentication-service"
  "usermanagement-service"
  "registry-service"
  "chess-service"
  "fitness-service"
  "music-service"
  "gateway-service"
  "website-service"
)

for svc in "${SERVICES[@]}"; do
  echo "Updating $svc to version $VERSION..."
  kubectl set image "deployment/${svc}" -n microservices "${svc}=docker.io/70131370/${svc}:${VERSION}"

done

echo "All deployments updated to version ${VERSION}."