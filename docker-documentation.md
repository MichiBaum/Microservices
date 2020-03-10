# General docker-compose

## Startup (in root folder (lifemanagement))
We've seen that we can create and start the containers, the networks, and the volumes defined in the configuration with *up*:

    docker-compose up
    
If you want to build it too:

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


