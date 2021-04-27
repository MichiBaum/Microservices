# General docker & docker-compose documentation
The official docker compose documentation can be found [here](https://docs.docker.com/compose/compose-file/).
Docker documentation general can be found [here](https://docs.docker.com/).

## Available Containers in docker-compose
There are these microservices:
- [Portainer](./portainer/README.md)
- [Zipkin](./zipkin/README.md)
- [Zipkin Elasticsearch](./zipkin/elasticsearch/README.md)
- [Zipkin Kibana](./zipkin/kibana/README.md)
- [Actuator Elasticsearch](./actuator/elasticsearch/README.md)
- [Actuator Kibana](./actuator/kibana/README.md)
- [Registry](../microservice/registry/README.md)
- [Admin](../microservice/admin/README.md)
- [Gateway](../microservice/gateway/README.md)

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

## Update 
Update one service in a docker-compose file. Replace <service_name> with the service name in the docker-compose file.

    docker-compose up -d --no-deps --build <service_name>

## See Logs
To see logs execute command:

    docker-compose logs

To follow the log output add *-f*

    docker-compose logs -f

## Open console in container
    docker exec -it <container name> /bin/bash

## Delete everything unused
The docker system prune command is a shortcut that prunes images, containers, and networks. In Docker 17.06.0 and earlier, volumes are also pruned. In Docker 17.06.1 and higher, you must specify the --volumes flag for docker system prune to prune volumes.

    docker system prune

    WARNING! This will remove:
            - all stopped containers
            - all networks not used by at least one container
            - all dangling images
            - all build cache
    Are you sure you want to continue? [y/N] y

If you are on Docker 17.06.1 or higher and want to also prune volumes, add the --volumes flag:

    docker system prune --volumes

    WARNING! This will remove:
            - all stopped containers
            - all networks not used by at least one container
            - all volumes not used by at least one container
            - all dangling images
            - all build cache
    Are you sure you want to continue? [y/N] y