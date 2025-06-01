# New Deployment

Every new Deployment is first starts with a pull request and merge into the master branch. This triggers multiple Github actions. 
One of these is [Build and Publish](Actions.md#deploy-yml-build-and-publish).
This action creates new docker images and uploads them to [Docker Hub](https://hub.docker.com/u/70131370).
