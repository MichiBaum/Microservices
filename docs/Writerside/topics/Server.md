# Server

Server is running Ubuntu and structured the following way:

- [Paths](#paths)
- [Running](#running)
- [Backup](#backup)

## Resources
Current Server (Ubuntu) has the following resources:
- 12GB Memory
- 8 Core
- ~ 250GB Disk

## Paths
The [git Project](https://github.com/MichiBaum/Microservices) is checked out under ``/git/Microservices``.  
Data is saved under ``/data``. For example every database volume has a folder like ``/data/chess-db``. [Sql backups](#backup) are saved in ``/data/db-backup``.  
Ssl certificates are saved in ``/data/ssl``


## Running
Every service is running in Kubernetes. You can find them in the folder kubernetes.


# Backup
The Server is backed up by the host provider.
The Database container have a volume bound so every restart contains the data.
Every Day at 02:00 the databases are backed up like described [here](Database.md#backup).
