# AI Guidelines & Project Context

This file provides critical context and rules for AI assistants (Junie, Claude Code, Cursor, GitHub Copilot, etc.) to follow when working on this microservices project.

## 🚀 Quick-start Checklist
- [ ] Read this file and `README.md` before acting.
- [ ] Check `docs/Writerside/topics/Guidelines.md` for general principles.
- [ ] If working on the frontend, follow `docs/Writerside/topics/UI-Style-Accessibility-Guidelines.md`.
- [ ] Ensure any new service follows the module naming convention (`*-service`).
- [ ] Update documentation if you change public APIs or architecture.

## 🛠️ Local Development Commands
| Task | Command | Directory |
|------|---------|-----------|
| Build entire project | `./mvnw clean install` | Root |
| Run a specific service | `./mvnw spring-boot:run` | Service directory |
| Install frontend deps | `npm install` | `website/` |
| Start frontend dev | `npm start` | `website/` |
| Run frontend tests | `npm test` | `website/` |
| Build frontend | `npm run build` | `website/` |
| Start local infra | `docker-compose -f docker-compose.local.yml up -d` | Root |

## 🏗️ Architecture & UI Guidelines
- **Microservices Independence**: Services must not have direct compile-time dependencies on each other. Use REST or event-driven communication.
- **Library Naming**: 
    - `*-library`: General library.
    - `spring-boot-starter-*`: Spring Boot starter.
- **Mobile-First UI**: 
    - NEVER use `hover:` classes.
    - Use responsive grid layouts.
    - Adhere to ARIA standards and semantic HTML.
- **Database**: Use MariaDB. Migrations should be handled via Flyway (check `src/main/resources/db/migration`).

## 🛡️ Security & Data Handling
- **Authentication**: Use the `authentication-service` and `authentication-library`.
- **Permissions**: Implement RBAC using the `permission-library`.
- **Sensitive Data**: Never log passwords, tokens, or PII.
- **Input Validation**: Sanitize all user inputs.

## 🧪 Testing & Quality
- **Unit Tests**: Required for all new business logic.
- **Integration Tests**: Use Testcontainers for service-level integration tests.
- **Architecture Tests**: Use ArchUnit (see `archunittests/`).
- **Clean Code**: Follow Kotlin coding conventions and use meaningful naming.

## ⚡ Tool Efficiency & Operational Utility
- **In-Memory Context Caching**: Never re-read an unchanged file or re-execute an identical search query already present in the active conversation context.
- **Targeted Tool Invocation**: Prefer specific file paths and targeted grep patterns over broad recursive directory searches.
- **Pre-Flight Syntax Validation**: Format terminal arguments and quote strings according to host shell standards before execution to avoid command failures.
- **Mandatory Verification**: Always execute automated verification (e.g. `./mvnw test`, `npm test`) on code changes before marking tasks completed.

## 🤖 Specialized Configuration
This project uses specialized **Subagents** and **Skills** to assist development.

### Specialized Subagents
- [Spring Microservices Architect](agents/spring-microservices-architect.md)
- [Angular UI/UX Specialist](agents/angular-ui-ux-specialist.md)
- [Cloud & Infrastructure Engineer](agents/cloud-infrastructure-engineer.md)
- [Security & Identity Expert](agents/security-identity-expert.md)
- [Quality & Documentation Guardian](agents/quality-documentation-guardian.md)
- [Testing & Code Review Expert](agents/testing-code-review-expert.md)
- [Product Management Specialist](agents/product-management-specialist.md)
- [Conversation Evaluator & Performance Optimizer](agents/conversation-evaluator.md)

### Core & Domain Skills
- [Global Skills](skills/global-skills/SKILL.md)
- [Conversation Evaluator](skills/conversation-evaluator/SKILL.md)
- [Agent Self-Improvement & Closed-Loop Optimization](skills/agent-self-improvement/SKILL.md)
- [Microservices Architecture](skills/microservices-architecture/SKILL.md)
- [Kotlin & Spring Mastery](skills/kotlin-spring-mastery/SKILL.md)
- [Persistence Optimization](skills/persistence-optimization/SKILL.md)
- [Database Expert](skills/database-expert/SKILL.md)
- [Performance Engineering](skills/performance-engineer/SKILL.md)
- [Release Engineering & Versioning](skills/release-engineer/SKILL.md)
- [UI & UX Skills](skills/ui-ux-skills/SKILL.md)
- [Tailwind Mastery](skills/tailwind-mastery/SKILL.md)
- [Angular Mastery](skills/angular-mastery/SKILL.md)
- [IAM Specialist](skills/iam-specialist/SKILL.md)
- [Security Best Practices](skills/security-best-practices/SKILL.md)
- [Infrastructure & Tooling](skills/infrastructure-tooling/SKILL.md)
- [Kubernetes Expert](skills/kubernetes-expert/SKILL.md)
- [Observability Mastery](skills/observability-mastery/SKILL.md)
- [Testing Mastery](skills/testing-polyglot/SKILL.md)
- [Deep Code Analysis](skills/deep-code-analysis/SKILL.md)
- [ArchUnit Expert](skills/archunit-expert/SKILL.md)
- [Documentation Standards](skills/documentation-standards/SKILL.md)
- [Design Tree & Plan Stress-Testing (Grill Me)](skills/grill-me/SKILL.md)
