# Database


## Updates
To update database's table, columns or anything [Flyway](Flyway.md) is used.


## Backup
A backup of a database can be done with this command:

```Bash
# Replace service, database and root password. Root password can be found in secrets.yaml
docker exec microservices-chess-db-1 mysqldump -u root -pROOTPASSWORD chess | gzip -c > /data/db-backup/chess_$(date -d "today" +"%Y-%m-%d_%H-%M").sql.gz
```

## Automatic backup
The backup described [above](#backup) is executed every day at 02:00 for all databases using Kubernetes CronJobs.

The database backups are managed by k3s CronJobs defined in the `db-backup.yaml` file. These CronJobs:
- Run at 2:00 AM daily
- Back up each database (chess, music, fitness, usermanagement, authentication)
- Compress the backups with gzip
- Store the backups in the `/data/db-backup` directory

To view the status of the backup jobs:
```bash
kubectl get cronjobs -n microservices
kubectl get jobs -n microservices
```

To view the logs of a specific backup job:
```bash
kubectl logs job/<job-name> -n microservices
```
