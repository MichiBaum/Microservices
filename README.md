# Microservices
This project is all about microservices and microfrontends.
There are microservices build with spring, spring cloud and so forth.

## Docker documentation
[Docker dokumentation](./docker/README.md)

## Available Microservices
There are these microservices:
- [Registry](./registry/README.md)
- [Admin](./admin/README.md)
- [Gateway](./gateway/README.md)

## Available Docker Containers
The available docker containers can be found [here](./docker/README.md)

## Build docker images
Build docker images:

    mvnw spring-boot:build-image -DdockerHub.username=YourDockerHubUsername -DdockerHub.password=YourDockerHubPassword


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
