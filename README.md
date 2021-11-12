   # Microservices
This project is all about microservices and microfrontends.
There are microservices build with spring, spring cloud and so forth.

## Available Microservices
There are these microservices:
- [Admin Service](./admin-service/README.md)
- [Javadoc Service](./javadoc-service/README.md)
- [Registry Service](registry-service/README.md)

## Release

### Maven release plugin

    mvnw release:update-versions

## Docker

### Build JavaDoc

    mvnw javadoc:aggregate

### Build & Push docker images
Build docker images:

    mvnw spring-boot:build-image --projects ./admin-service,./javadoc-service,./registry-service -DdockerHub.username=YourDockerHubUsername -DdockerHub.password=YourDockerHubPassword

### Scan docker files

    docker scan YourDockerHubUsername/DockerHubRepository:TagName --dependency-tree


## License
It is licensed under the [Apache License Version 2.0](LICENSE).

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
