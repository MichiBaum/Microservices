# Microservice
This folder contains microservices that are build with spring, spring boot, spring cloud and so forth.  
It also contains [python scripts](./python_scripts/README.md) which simplify some commands.  
The microservices run in Docker containers and these are defined in [docker-compose](docker-compose.yml).  

## Available Microservices
There are these microservices:
- [config](./config/README.md)
- [registry](./registry/README.md)
- [admin](./admin/README.md)
- [gateway](./gateway/README.md)
- [monitoring](./monitoring/README.md)

## build docker images
Run:

    mvnw spring-boot:build-image

