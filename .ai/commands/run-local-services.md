---
name: run-local-services
description: Start all local microservices using Docker Compose.
---
Execute `docker-compose -f docker-compose.local.yml up -d` in the root directory to start all local services.
Verify that all containers are running and healthy by checking `docker ps`.
Confirm that services are responding on their expected ports.
