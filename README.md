   # Microservices
This project is all about microservices and microfrontends.
There are microservices build with spring, spring cloud and so forth.

## Available Microservices
There are these microservices:
- [Admin](./admin/README.md)
- [Javadoc](./javadoc/README.md)

## Docker

### Build & Push docker images
Build docker images:

    mvnw spring-boot:build-image -DdockerHub.username=YourDockerHubUsername -DdockerHub.password=YourDockerHubPassword

### Scan docker files

    docker scan YourDockerHubUsername/DockerHubRepository:TagName --dependency-tree

## Keycloak add user

    .../bin/add-user-keycloak.sh -r master -u <username> -p <password>

## Kubernetes

[Kubernetes README.md](./k8s/README.md)

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
