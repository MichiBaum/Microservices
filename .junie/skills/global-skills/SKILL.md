---
name: "global-skills"
description: "Baseline expertise and project-wide standards shared across all Junie subagents. Use this skill for any task in the project to ensure consistency, guideline compliance, and cross-service awareness."
---
# Global Skills

These global skills define the baseline for all assistance in this microservices project.

## When to Use
- Always active for every task in this project.
- Provides the foundational rules that all other skills build upon.

## General Principles
- **Guideline Awareness**: Before making changes, read and follow all project guidelines in `docs/Writerside/topics/`.
- **Consistency**: Match existing code style, naming conventions, and architectural patterns in the file and module you are editing.
- **Contextual Awareness**: Consider the impact of changes on the broader microservices ecosystem. A change in a library affects all consuming services.
- **Proactive Communication**: Clarify ambiguous requirements before implementing. Report potential issues or risks early.

## Project Structure Rules
- Deployable services are named `*-service` (e.g., `authentication-service`, `chess-service`).
- Shared libraries are named `*-library` (e.g., `permission-library`) or `spring-boot-starter-*` (e.g., `spring-boot-starter-authentication`).
- Frontend code lives in `website/` and is built with Angular.
- Documentation lives in `docs/Writerside/topics/`.

## Workflow Standards
1. Read relevant guidelines and existing code before making changes.
2. Keep changes minimal and focused on the task at hand.
3. Identify all affected services and modules before proposing changes.
4. Update documentation if you change public APIs, architecture, or user-facing behavior.
5. Never log passwords, tokens, or PII.
