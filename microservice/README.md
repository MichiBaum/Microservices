# Microservice
This folder contains microservices that are build with spring, spring boot, spring cloud and so forth.  
The microservices run in Docker containers, and these are defined in [docker-compose](../docker/docker-compose.yml).  

## Available Microservices
There are these microservices:
- [Registry](./registry/README.md)
- [Admin](./admin/README.md)
- [Gateway](./gateway/README.md)

## Available Docker Containers
The available docker containers can be found [here](../docker/README.md)

## Build docker images
Build docker images:

    mvnw spring-boot:build-image
