DATE = date +"%m-%d-%Y"
TIME = date +"%T"
DATETIME = $DATE + $TIME
docker exec lifemanagement-database sh -c 'exec mysqldump --all-databases -u root --password=root' > /lifemanagement/database/backup/$DATETIME.sql