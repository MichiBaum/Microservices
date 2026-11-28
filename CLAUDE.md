# Claude Code Configuration & Guidelines

Please adhere strictly to the project architecture, conventions, and guidelines defined in `.ai/AGENTS.md`.

## Key Resources
- **Guidelines & Architecture**: [.ai/AGENTS.md](.ai/AGENTS.md)
- **Domain Skills**: [.ai/skills/](.ai/skills/)
- **Specialized Agents**: [.ai/agents/](.ai/agents/)
- **Workflow Commands**: [.ai/commands/](.ai/commands/)

## Summary of Conventions
- **Build**: `./mvnw clean install` (Root)
- **Architecture**: Microservices independence; no direct compile-time service-to-service dependencies.
- **Frontend**: Mobile-first UI, Tailwind CSS, Angular (never use `hover:` classes).
- **Backend**: Kotlin & Spring Boot, MariaDB, Flyway migrations.
- **Quality**: Unit tests required, integration tests with Testcontainers, architecture tests with ArchUnit.
