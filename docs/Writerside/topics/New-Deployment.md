# New Deployment

Every new Deployment is first starts with a pull request and merge into the master branch. This triggers multiple Github actions. 
One of these is [Build and Publish](Actions.md#deploy-yml-build-and-publish).
This action creates new docker images and uploads them to [Docker Hub](https://hub.docker.com/u/70131370).

## Step 1
Make a database backup. This is described [here](Database.md#backup).

## Step 2
Shut down docker containers. Either the ones affected by the changes or you can shut down all:

```bash
# Shut down two specific
docker compose down website-service chess-service

# Shut down all
docker compose down
```

## Step 3
Pull master changes

```bash
# Fetch changes
git fetch

# Pull changes
git pull
```

## Step 4
Remove unused Images. These Images are the ones from the containers which we shut down.  
This step is only needed if the versions of the docker images have not changed.

```bash
docker image prune -a
```

## Step 5
Start new Containers with new Images

```bash
# Start specific containers
docker compose up -d website-service chess-service

# Start all containers
docker compose up -d

```