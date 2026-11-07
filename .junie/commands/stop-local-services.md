---
name: stop-local-services
description: Stop all local microservices using Docker Compose.
---
Execute `docker-compose -f docker-compose.local.yml down` in the root directory to stop all local services.
Verify that all containers have been stopped by checking `docker ps`.
Clean up unused resources if needed with `docker-compose -f docker-compose.local.yml down -v`.
