# General docker & docker-compose documentation
The official docker compose documentation can be found [here](https://docs.docker.com/compose/compose-file/).
Docker documentation general can be found [here](https://docs.docker.com/).

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