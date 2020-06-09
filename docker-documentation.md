# General docker & docker-compose documentation

## Startup (in root folder (lifemanagement))
We've seen that we can create and start the containers, the networks, and the volumes defined in the configuration with *up*:

    docker-compose up --build
    
After the first time, however, we can simply use start to *start* the services:

    docker-compose start

In case our file has a different name than the default one (*docker-compose.yml*), we can exploit the *-f* and *- -file*  flags to specify an alternate file name:

    docker-compose -f custom-compose-file.yml start

Compose can also run in the background as a daemon when launched with the *-d* option:

    docker-compose up -d

## Shutdown (in root folder (lifemanagement))
To safely stop the active services, we can use *stop*, which will preserve containers, volumes, and networks, along with every modification made to them:

    docker-compose stop

To reset the status of our project, instead, we simply run *down*, **which will destroy everything with only the exception of external volumes**:

    docker-compose down

## See Logs (in root folder (lifemanagement))
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