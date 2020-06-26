### Backup database folder

DIR="/docker/lifemanagement/database/backup/"
DIRBACKUP="/docker/lifemanagement/database/db_backup/"
DATETIME=$(date +%Y%m%d%H%M%S)

if [ -d "$DIRBACKUP" ]; then

  ### Take action if $DIRBACKUP exists ###
  echo "Directory ${DIRBACKUP} exists"

else

  ###  Control will jump here if $DIRBACKUP does NOT exists ###
  echo "Directory ${DIRBACKUP} does not exist"
  mkdir -p $DIRBACKUP
  echo "Directory ${DIRBACKUP} created"

fi

if [ -d "$DIR" ]; then
  ### Take action if $DIR exists ###
  echo "Directory ${DIR} exists"

  if [ -z "$(ls -A -- "$DIR")" ]; then

    ### Take action if $DIR has NO content ###
    echo "Directory ${DIR} IS EMPTY"

    if [ -z "$(ls -A -- "$DIRBACKUP")" ]; then

      echo "Directory ${DIRBACKUP} IS EMPTY"
      exit 1

    else

      echo "Directory ${DIRBACKUP} has content"
      cd $DIRBACKUP
      $BACKUP = find $DIRBACKUP -mindepth 1 -maxdepth 1 -type d | sort -rn | head -1
      tar zxvf $BACKUP --directory $DIR
      exit 1

    fi
    exit 1

  else

    ###  Control will jump here if $DIR has content ###
    echo "Directory ${DIR} has content"
    cd $DIRBACKUP
    tar zcvf $DATETIME.tar.gz --absolute-names $DIR
    exit 1

  fi
  exit 1

else

  ###  Control will jump here if $DIR does NOT exists ###
  echo "Directory ${DIR} does not exist"
  mkdir -p $DIR
  echo "Directory ${DIR} created"

  if [ -z "$(ls -A -- "$DIRBACKUP")" ]; then

    exit 1

  else

    cd $DIRBACKUP
    $BACKUP = find $DIRBACKUP -mindepth 1 -maxdepth 1 -type d | sort -rn | head -1
    tar zxvf $BACKUP --directory $DIR
    exit 1

  fi
  exit 1

fi
exit 1