# Database


## Updates
To update database's table, columns or anything [Flyway](Flyway.md) is used.


## Backup
A backup of a database can be done with this command:

```Bash
# Replace service, database and root password. Root password can be found in .env
docker exec microservices-chess-db-1 mysqldump -u root -pROOTPASSWORD chess | gzip -c > /data/db-backup/chess_$(date -d "today" +"%Y-%m-%d_%H-%M").sql.gz
```

## Automatic backup
The backup described [above](#backup) is executed every day at 02:00 for all databases with a cronjob.  
The date variable ``$(date ...)`` needs a change when run as cronjob. YOu need to escape ``%`` like ``\%``

```Bash
# Show all jobs (crontab -l)
root@35591:/ crontab -l
...
# Everyday at 02:00 do a sqldump of Microservices docker databases
0 2 * * * docker exec microservices-chess-db-1 mysqldump -u root -pROOTPASSWORD chess | gzip -c > /data/db-backup/chess_$(date -d "today" +"\%Y-\%m-\%d_\%H-\%M").sql.gz
0 2 * * * docker exec microservices-music-db-1 mysqldump -u root -pROOTPASSWORD music | gzip -c > /data/db-backup/music_$(date -d "today" +"\%Y-\%m-\%d_\%H-\%M").sql.gz
0 2 * * * docker exec microservices-fitness-db-1 mysqldump -u root -pROOTPASSWORD fitness | gzip -c > /data/db-backup/fitness_$(date -d "today" +"\%Y-\%m-\%d_\%H-\%M").sql.gz
0 2 * * * docker exec microservices-usermanagement-db-1 mysqldump -u root -pROOTPASSWORD usermanagement | gzip -c > /data/db-backup/usermanagement_$(date -d "today" +"\%Y-\%m-\%d_\%H-\%M").sql.gz
0 2 * * * docker exec microservices-authentication-db-1 mysqldump -u root -pROOTPASSWORD authentication | gzip -c > /data/db-backup/authentication_$(date -d "today" +"\%Y-\%m-\%d_\%H-\%M").sql.gz

# edit jobs
crontab -e

```