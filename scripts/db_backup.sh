#Backup database folder

DIR="/docker/lifemanagement/database/backup/"
DIRBACKUP="/docker/lifemanagement/database/db_backup/"
DATETIME=$(date +%Y%m%d%H%M%S)

if [ -d "$DIRBACKUP" ]; then
  echo "Directory ${DIRBACKUP} exists"
else
  echo "Directory ${DIRBACKUP} does not exist"
  mkdir -p $DIRBACKUP
  echo "Directory ${DIRBACKUP} created"
fi

if [ -d "$DIR" ]; then
  ### Take action if $DIR exists ###
  echo "Directory ${DIR} exists"
  if [ -z "$(ls -A -- "$DIR")" ]; then
    $BACKUP = find $DIRBACKUP -mindepth 1 -maxdepth 1 -type d | sort -rn | head -1
    tar -C $DIR -zxvfP $BACKUP
  else
    tar -zcvfP $DATETIME.tar.gz $DIRBACKUP
  fi
  exit 1
else
  ###  Control will jump here if $DIR does NOT exists ###
  echo "Directory ${DIR} does not exist"
  mkdir -p $DIR
  echo "Directory ${DIR} created"
  $BACKUP = find $DIRBACKUP -mindepth 1 -maxdepth 1 -type d | sort -rn | head -1
  tar -C $DIR -zxvfP $BACKUP
  exit 1
fi
exit 1