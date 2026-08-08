---
name: "infrastructure-tooling"
description: "Proficiency in local development environment, CLI tools, Docker Compose, and build systems. Use when setting up dev environments, running builds, managing Docker containers, or troubleshooting tooling issues."
---
# Infrastructure & Tooling

## When to Use
- Setting up or troubleshooting the local development environment.
- Running Maven or NPM builds and resolving build failures.
- Managing local Docker containers via `docker-compose.local.yml`.
- Configuring or debugging CI/CD pipelines (GitHub Actions).

## Local Development Commands
| Task | Command | Directory |
|------|---------|-----------|
| Build entire project | `./mvnw clean install` | Root |
| Run a specific service | `./mvnw spring-boot:run` | Service directory |
| Install frontend deps | `npm install` | `website/` |
| Start frontend dev | `npm start` | `website/` |
| Run frontend tests | `npm test` | `website/` |
| Build frontend | `npm run build` | `website/` |
| Start local infra | `docker-compose -f docker-compose.local.yml up -d` | Root |

## Build System
- **Maven** is the build tool for all backend services and libraries.
- Use the Maven wrapper (`./mvnw`) to ensure consistent versions across environments.
- The root `pom.xml` manages all dependency versions and module declarations.
- **NPM** is used for the Angular frontend in `website/`.

## Docker & Local Infrastructure
- `docker-compose.local.yml` defines the local development stack (databases, services).
- Use `docker-compose -f docker-compose.local.yml up -d` to start all infrastructure.
- Use `docker-compose -f docker-compose.local.yml down` to stop and clean up.
- Dockerfiles should use `eclipse-temurin:25-jre-alpine` as the base image for services.

## Terminal Environment
- This project runs on Windows; use PowerShell syntax for all terminal commands.
- Use `;` to chain commands in PowerShell (not `&&`).
- Use backslashes `\` for file paths.

## Constraints
- Do not install global packages without documenting the requirement.
- Do not modify the Maven wrapper files (`mvnw`, `mvnw.cmd`, `.mvn/`).
