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
- portainer
- zipkin

## Build docker images
Build docker images:

    mvnw spring-boot:build-image

Or you can run it via [python script](./python_scripts/maven.md):

    python ./python_scripts/maven.py

## Run
First you have to run the 'Build docker images' step.  
Then you can run the docker containers with docker compose:

    docker-compose up --build -d
    
or via [python script](./python_scripts/docker.md):

    python ./python_scripts/docker.py -startAll
    
If you want to renew a container you have to run the 'Build docker images' step and then:

    docker-compose up -d --no-deps --build <containernames>
    
this can also be run via [python script](./python_scripts/docker.md) but you have to run the 'Build docker images' step first too:

    python ./python_scripts/docker.py -containers <containernames>
