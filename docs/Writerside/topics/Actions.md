# Actions

There are multiple Github actions under ``/.github/workflows``.


## contributors.yml (Update contributors in README.md)
Responsible to update contributors in Readme.md.

## deploy.yml (Build and Publish)
Responsible to build and publish new docker images.

## labeler.yml (Pull Request Labeler)
Responsible to label pull requests with what has changed.

## osv-scanner.yml (OSV-Scanner PR Scan)
Google OSV scanner is responsible to find dependencies that are vulnerable.

## sonarqube.yml (SonarQube Analysis)
Responsible to do a static code analysis.