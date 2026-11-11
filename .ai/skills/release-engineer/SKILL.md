---
name: release-engineer
description: Expert in release management, versioning, changelogs, and deployment automation for the Microservices project
---

# Release Engineer

Use this skill when managing releases, versioning, changelogs, deployment automation, or preparing services for production.

## When to Use

- Preparing a new release of a service or library
- Writing changelog entries for changes
- Managing version numbers across services
- Configuring deployment pipelines
- Planning release schedules
- Coordinating cross-service releases

## Version Management

### Semantic Versioning
- Use `MAJOR.MINOR.PATCH` format
- `MAJOR`: Breaking changes
- `MINOR`: New features (backward compatible)
- `PATCH`: Bug fixes (backward compatible)

### Maven Versioning
```xml
<!-- pom.xml -->
<version>1.2.3</version>
```

### Version Consistency
- Ensure version is consistent across:
  - `pom.xml`
  - `README.md`
  - GitHub tags
  - Docker image tags
  - Kubernetes manifests

## Changelog Guidelines

### Writerside Documentation
- All changelogs live in `docs/Writerside/topics/`
- Follow existing naming conventions: `<Date>_<Feature>.md`

### Changelog Entry Format
```markdown
# Changelog

## [1.2.3] - 2026-11-04

### Added
- New endpoint for user management
- Support for OAuth2 authentication

### Changed
- Updated dependency versions
- Improved error messages

### Fixed
- Resolved null pointer in UserService
- Fixed database connection timeout

### Removed
- Deprecated `getUserByLegacyId` method
```

### Changelog Categories
- **Added**: New features and capabilities
- **Changed**: Modifications to existing functionality
- **Fixed**: Bug fixes and issue resolutions
- **Removed**: Deprecated or removed features
- **Security**: Security-related changes

## Release Workflow

### Pre-Release Checklist

1. **Code Quality**
   - [ ] All tests passing (`./mvnw test`)
   - [ ] Code coverage meets thresholds
   - [ ] ArchUnit tests passing

2. **Documentation**
   - [ ] Changelog updated with all changes
   - [ ] API documentation current
   - [ ] README updated if needed

3. **Build Verification**
   - [ ] Full build succeeds (`./mvnw clean install`)
   - [ ] Docker build succeeds
   - [ ] Kubernetes manifests valid

### Release Steps

1. **Update Version**
   ```bash
   # Update pom.xml
   ./mvnw versions:set -DnewVersion=1.2.4
   
   # Commit changes
   git commit -m "chore: update version to 1.2.4"
   ```

2. **Update Changelog**
   - Add entries under the appropriate version section
   - Mark as unreleased if not final

3. **Finalize Release**
   - Close or move issue/PR to "Ready for Release"
   - Tag the release in Git
   - Create GitHub release

## Deployment Automation

### Docker Configuration
```dockerfile
FROM eclipse-temurin:26-jre-alpine
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: chess-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: chess-service
  template:
    metadata:
      labels:
        app: chess-service
    spec:
      containers:
      - name: chess-service
        image: chess-service:1.2.4
        ports:
        - containerPort: 8080
```

### CI/CD Integration
- GitHub Actions workflows in `.github/workflows/`
- Automated builds on push to `main`
- Automated deployments to staging/production

## Output Standards

- Provide exact version numbers for all components
- Include complete changelog entries
- Specify deployment commands and scripts
- List any breaking changes with migration guides

## Project-Specific Notes

- Use semantic versioning across all services
- Coordinate releases across related services
- Maintain changelogs for all services
- Use GitHub releases for version tagging
- Deploy with Kubernetes for production