# Docker

Many things about docker and deployment are already written in [New Deployment](New-Deployment.md).

## Logs
To see the logs of the deployment you can use following:

```Bash
# Follow all logs from all services
docker compose logs -f

# Follow logs from one service
docker compose logs -f chess-service
```
If you dont want to follow the logs just remove ``-f``.


## Stats
To see the resource stats of the containers execute following:

```Bash
docker stats
```