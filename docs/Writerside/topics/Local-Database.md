# Local Database

## Local dev db
Create a local dev db with:
```Bash
docker run --name microservices -e MARIADB_ROOT_PASSWORD=someRootPass -p 3306:3306 -d mariadb:10.11
```

After that create the databases in the container.

```Bash
create schema `alexandria-db`;
create schema `authentication-db`;
create schema `chess-db`;
create schema `fitness-db`;
create schema `music-db`;
create schema `usermanagement-db`;
```

The tables are created trough [flyway](Flyway.md).