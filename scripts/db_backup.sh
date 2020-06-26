### Backup database folder

DIR="/docker/lifemanagement/database/backup/"
DIRBACKUP="/docker/lifemanagement/database/db_backup/"
DATETIME=$(date +%Y%m%d%H%M%S)

if [ -d "$DIRBACKUP" ]; then

  ### Take action if $DIRBACKUP exists ###
  echo -e "$(c g)Directory ${DIRBACKUP} exists"

else

  ###  Control will jump here if $DIRBACKUP does NOT exists ###
  echo -e "$(c r)Directory ${DIRBACKUP} does not exist"
  mkdir -p $DIRBACKUP
  echo -e "$(c g)Directory ${DIRBACKUP} created"

fi

if [ -d "$DIR" ]; then
  ### Take action if $DIR exists ###
  echo -e "$(c g)Directory ${DIR} exists"

  if [ -z "$(ls -A -- "$DIR")" ]; then

    ### Take action if $DIR has NO content ###
    echo -e "$(c r)Directory ${DIR} IS EMPTY"

    if [ -z "$(ls -A -- "$DIRBACKUP")" ]; then

      echo -e "$(c r)Directory ${DIRBACKUP} IS EMPTY"
      exit 1

    else

      echo -e "$(c g)Directory ${DIRBACKUP} has content"
      cd $DIRBACKUP
      $BACKUP = find $DIRBACKUP -mindepth 1 -maxdepth 1 -type d | sort -rn | head -1
      tar zxvf $BACKUP --directory $DIR
      exit 1

    fi
    exit 1

  else

    ###  Control will jump here if $DIR has content ###
    echo -e "$(c g)Directory ${DIR} has content"
    cd $DIRBACKUP
    tar zcvf $DATETIME.tar.gz --absolute-names $DIR
    exit 1

  fi
  exit 1

else

  ###  Control will jump here if $DIR does NOT exists ###
  echo -e "$(c r)Directory ${DIR} does not exist"
  mkdir -p $DIR
  echo -e "$(c g)Directory ${DIR} created"

  if [ -z "$(ls -A -- "$DIRBACKUP")" ]; then

    echo -e "$(c r)Directory ${DIRBACKUP} IS EMPTY"
    exit 1

  else

    echo -e "$(c g)Directory ${DIRBACKUP} has content"
    cd $DIRBACKUP
    $BACKUP = find $DIRBACKUP -mindepth 1 -maxdepth 1 -type d | sort -rn | head -1
    tar zxvf $BACKUP --directory $DIR
    exit 1

  fi
  exit 1

fi
exit 1