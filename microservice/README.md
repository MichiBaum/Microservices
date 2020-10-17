# Microservice
This folder contains microservices that are build with spring, spring boot, spring cloud and so forth.  
The microservices run in Docker containers and these are defined in [docker-compose](docker-compose.yml).  

## Available Microservices
There are these microservices:
- [registry](./registry/README.md)
- [admin](./admin/README.md)
- [gateway](./gateway/README.md)
- [portainer](./portainer/README.md)
- zipkin
- zipkin-elasticsearch

## Build docker images
Build docker images:

    mvnw spring-boot:build-image

## Run
First you have to run the 'Build docker images' step.  
Then you can run the docker containers with docker compose:

    docker-compose up --build -d

If you want to renew a container you have to run the 'Build docker images' step and then:

    docker-compose up -d --no-deps --build <containernames>
