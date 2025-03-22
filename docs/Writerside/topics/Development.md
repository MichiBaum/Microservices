# Development

Requirements:
- Java 21 installed
- Npm installed (lts is best)
- [Update hosts file](#update-hosts-file)
- Local dev database



## Update hosts file
*Remember to take Backup before editing your `hosts` file, mistakes there can block your internet access or cause other network-related issues.*


### Windows
You need Administrator access to update hosts.  
In ``C:\Windows\System32\drivers\etc`` find file *hosts* and add those lines (add domain depending on case):

```
# Added Manually
# Microservices
127.0.0.1 chess.michibaum.ch
127.0.0.1 gateway.michibaum.ch
127.0.0.1 registry.michibaum.ch
127.0.0.1 admin.michibaum.ch
127.0.0.1 usermanagement.michibaum.ch
127.0.0.1 authentication.michibaum.ch
127.0.0.1 fitness.michibaum.ch
127.0.0.1 music.michibaum.ch
127.0.0.1 michibaum.ch
```

### Linux
In Linux the file is located under ``/etc/hosts``.

## Local dev db
Create a local dev db with:
```Bash
docker run --name microservices -e MARIADB_ROOT_PASSWORD=someRootPass -p 3306:3306 -d mariadb:10.11
```

After that create the databases in the container.

```Bash
create schema `authentication-db`;
create schema `chess-db`;
create schema `fitness-db`;
create schema `music-db`;
create schema `usermanagement-db`;
```

The tables are created trough [flyway](Flyway.md).