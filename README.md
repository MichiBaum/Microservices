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



This project is all about microservices and microfrontends.
There are microservices build with spring, spring cloud and so forth.

## Available Microservices
There are these microservices:
- [Admin Service](./admin-service/README.md)
- [Authentication Service](./authentication-service/README.md)
- [Javadoc Service](./javadoc-service/README.md)
- [Registry Service](./registry-service/README.md)
- [Usermanagement Service](./usermanagement-service/README.md)

## Release

### Maven release plugin

    mvnw release:update-versions

## Docker

### Build JavaDoc

    mvnw javadoc:aggregate

### Build & Push docker images
Build docker images:

    mvnw spring-boot:build-image --projects ./admin-service,./javadoc-service,./registry-service -DdockerHub.username=YourDockerHubUsername -DdockerHub.password=YourDockerHubPassword

### Change HOSTS file

#### Windows

In *C:\Windows\System32\drivers\etc* find file *hosts* and add those lines:

    # Microservises
    127.0.0.1 registry.michibaum.ch
    127.0.0.1 admin.michibaum.ch
    127.0.0.1 javadoc.michibaum.ch
    127.0.0.1 usermanagement.michibaum.ch
    127.0.0.1 authentication.michibaum.ch

#### Linux

File: */etc/hosts*


### Scan docker files

    docker scan YourDockerHubUsername/DockerHubRepository:TagName --dependency-tree

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
