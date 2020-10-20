# Microservice
This folder contains microservices that are build with spring, spring boot, spring cloud and so forth.  
The microservices run in Docker containers, and these are defined in [docker-compose](docker-compose.yml).  

## Available Microservices (docker containers)
There are these microservices (docker containers):
- [Portainer](./portainer/README.md)
- [Zipkin](./zipkin/README.md)
- [Zipkin Elasticsearch](./zipkin/elasticsearch/README.md)
- [Zipkin Kibana](./zipkin/kibana/README.md)
- [Actuator Elasticsearch](./actuator/elasticsearch/README.md)
- [Actuator Kibana](./actuator/kibana/README.md)
- [Registry](./registry/README.md)
- [Admin](./admin/README.md)
- [Gateway](./gateway/README.md)

## Build docker images
Build docker images:

    mvnw spring-boot:build-image

## Run
First you have to run the 'Build docker images' step.  
Then you can run the docker containers with docker compose:

    docker-compose up --build -d

If you want to renew a container you have to run the 'Build docker images' step and then:

    docker-compose up -d --no-deps <containernames>

### Suggestion
Run the docker containers in the correct order with:

    docker-compose up -d --no-deps <containernames>
    
## Dependencies between containers

| Name                   | Depends On                                      |
|------------------------|-------------------------------------------------|
| Portainer              | -                                               |
| Zipkin                 | -Zipkin Elasticsearch                           |
| Zipkin Elasticsearch   | -                                               |
| Zipkin Kibana          | -Zipkin Elasticsearch                           |
| Zipkin Dependencies    | -Zipkin Elasticsearch                           |
| Actuator Elasticsearch | -                                               |
| Actuator Kibana        | -Actuator Elasticsearch                         |
| Registry               | -Zipkin<br>-Actuator Elasticsearch              |
| Admin                  | -Registry<br>-Zipkin<br>-Actuator Elasticsearch |
| Gateway                | -Registry<br>-Zipkin<br>-Actuator Elasticsearch |

Table generated with [https://www.tablesgenerator.com/markdown_tables](https://www.tablesgenerator.com/markdown_tables)
