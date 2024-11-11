# Microservices

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-orange.svg)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)  
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=MichiBaum_Microservices&metric=bugs)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=MichiBaum_Microservices&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=MichiBaum_Microservices&metric=coverage)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=MichiBaum_Microservices&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=MichiBaum_Microservices&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=MichiBaum_Microservices&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=MichiBaum_Microservices&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=MichiBaum_Microservices&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=MichiBaum_Microservices&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=MichiBaum_Microservices)


This project is all about microservices. Frontend is currently build with Angular.
There are microservices build with spring, spring cloud and so forth.


![Alt](https://repobeats.axiom.co/api/embed/69bb8de5fa17a6f9aa3c3c0ffad238a1e056edac.svg "Repobeats analytics image")

## Modules
There are these microservices:
- [Admin Service](./admin-service/README.md)
- [Authentication Service](./authentication-service/README.md)
- [Gateway Service](./gateway-service/README.md)
- [Registry Service](./registry-service/README.md)
- [Usermanagement Service](./usermanagement-service/README.md)
- [Website Service](./website-service/README.md)
- [Chess Service](./chess-service/README.md)
- [Fitness Service](./fitness-service/README.md)
- [Music Service](./music-service/README.md)

And these libraries:
- [Authentication Library](./authentication-library/README.md)
- [Permission Library](./permission-library/README.md)
- [Usermanagement Library](./usermanagement-library/README.md)
- [Spring Boot Starter Discord](./spring-boot-starter-discord/README.md)

And these databases:
- Authentication DB
- Usermanagement DB
- Chess DB
- Fitness DB

## Release

### Maven release plugin

The Maven Release Plugin automates the process of releasing project versions by updating version numbers and creating
tags in the version control system. Use the command `mvn release:update-versions` to update the version information in
your project's `pom.xml` files.

## Docker

### Container

#### Up

    docker compose up -d

#### Down

    docker compose down

#### Logs

    # Follow
    docker compose logs -f

    # Single container 
    docker compose logs ´name´

#### Stats

    docker stats

### Change HOSTS file (for dev on local machine)
*Remember to take Backup before editing your `hosts` file, mistakes there can block your internet access or cause other network-related issues.*


#### Windows

In *C:\Windows\System32\drivers\etc* find file *hosts* and add those lines:

    # Microservices
    127.0.0.1 chess.michibaum.ch
    127.0.0.1 gateway.michibaum.ch
    127.0.0.1 registry.michibaum.ch
    127.0.0.1 admin.michibaum.ch
    127.0.0.1 usermanagement.michibaum.ch
    127.0.0.1 authentication.michibaum.ch
    127.0.0.1 michibaum.ch

#### Linux

File: */etc/hosts*


### Scan docker files

    docker scan YourDockerHubUsername/DockerHubRepository:TagName --dependency-tree

## Local DB

    docker run --name microservices -e MARIADB_ROOT_PASSWORD=someRootPass -p 3306:3306 -d mariadb:lts-noble

After that create the databases in the container.
The tables are created trough

    spring
      jpa:
        hibernate:
          ddl-auto: update

## License

[![GitHub license](https://badgen.net/github/license/MichiBaum/Microservices)](https://github.com/MichiBaum/Microservices/blob/master/LICENSE)

## Collaborators

<!-- readme: collaborators -start -->
<table>
<tr>
    <td align="center">
        <a href="https://github.com/MichiBaum">
            <img src="https://avatars.githubusercontent.com/u/36712219?v=4" width="100;" alt="MichiBaum"/>
            <br />
            <sub><b>Michael Baumberger</b></sub>
        </a>
    </td></tr>
</table>
<!-- readme: collaborators -end -->

## Contributors

<!-- readme: SeverinNauer,contributors -start -->
<table>
<tr>
    <td align="center">
        <a href="https://github.com/severinnauer">
            <img src="https://avatars.githubusercontent.com/u/43473975?v=4" width="100;" alt="severinnauer"/>
            <br />
            <sub><b>Severin</b></sub>
        </a>
    </td>
    <td align="center">
        <a href="https://github.com/MichiBaum">
            <img src="https://avatars.githubusercontent.com/u/36712219?v=4" width="100;" alt="MichiBaum"/>
            <br />
            <sub><b>Michael Baumberger</b></sub>
        </a>
    </td></tr>
</table>
<!-- readme: SeverinNauer,contributors -end -->
