### Backup database folder

DIR="/docker/lifemanagement/database/backup/"
DIRBACKUP="/docker/lifemanagement/database/db_backup/"
DATETIME=$(date +%Y%m%d%H%M%S)

if [ -d "$DIRBACKUP" ]; then

  ### Take action if $DIRBACKUP exists ###
  echo -e "\e[0;32m Directory ${DIRBACKUP} exists \e[0m"

else

  ###  Control will jump here if $DIRBACKUP does NOT exists ###
  echo -e "\e[0;31m  Directory ${DIRBACKUP} does not exist \e[0m"
  mkdir -p $DIRBACKUP
  echo -e "\e[0;32m Directory ${DIRBACKUP} created \e[0m"

fi

if [ -d "$DIR" ]; then
  ### Take action if $DIR exists ###
  echo -e "\e[0;32m Directory ${DIR} exists \e[0m"

  if [ -z "$(ls -A -- "$DIR")" ]; then

    ### Take action if $DIR has NO content ###
    echo -e "\e[0;31m Directory ${DIR} IS EMPTY \e[0m"

    if [ -z "$(ls -A -- "$DIRBACKUP")" ]; then

      echo -e "\e[0;32m Directory ${DIRBACKUP} IS EMPTY \e[0m"
      exit 1

    else

      echo -e "\e[0;32m Directory ${DIRBACKUP} has content \e[0m"
      cd $DIRBACKUP
      $BACKUP = find $DIRBACKUP -mindepth 1 -maxdepth 1 -type d | sort -rn | head -1
      tar zxvf $BACKUP --directory $DIR
      exit 1

    fi
    exit 1

  else

    ###  Control will jump here if $DIR has content ###
    echo -e "\e[0;32m Directory ${DIR} has content \e[0m"
    cd $DIRBACKUP
    tar zcvf $DATETIME.tar.gz --absolute-names $DIR
    exit 1

  fi
  exit 1

else

  ###  Control will jump here if $DIR does NOT exists ###
  echo -e "\e[0;31m Directory ${DIR} does not exist \e[0m"
  mkdir -p $DIR
  echo -e "\e[0;32m Directory ${DIR} created \e[0m"

  if [ -z "$(ls -A -- "$DIRBACKUP")" ]; then

    echo -e "\e[0;31m Directory ${DIRBACKUP} IS EMPTY \e[0m"
    exit 1

  else

    echo -e "\e[0;32m Directory ${DIRBACKUP} has content \e[0m"
    cd $DIRBACKUP
    $BACKUP = find $DIRBACKUP -mindepth 1 -maxdepth 1 -type d | sort -rn | head -1
    tar zxvf $BACKUP --directory $DIR
    exit 1

  fi
  exit 1

fi
exit 1