# Maven

Maven is used in this project. Every service should have a .mvn and a ``mvnw``/``mvnw.cmd``.  

## Update version
The Maven Release Plugin automates the process of releasing project versions by updating version numbers and creating
tags in the version control system. Use the command `mvn release:update-versions` to update the version information in
your project's `pom.xml` files.