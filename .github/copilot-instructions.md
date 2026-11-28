# GitHub Copilot Instructions

Please review and adhere strictly to the project architecture, conventions, and guidelines defined in `.ai/AGENTS.md`.

## Key References
- **Guidelines & Architecture**: [.ai/AGENTS.md](../.ai/AGENTS.md)
- **Domain Skills**: [.ai/skills/](../.ai/skills/)
- **Specialized Agents**: [.ai/agents/](../.ai/agents/)
- **Workflow Commands**: [.ai/commands/](../.ai/commands/)

## Core Guidelines
- Follow the quick-start checklist and local development commands in `.ai/AGENTS.md`.
- Services must not have direct compile-time dependencies on each other (REST or event-driven communication only).
- In the frontend, follow mobile-first UI standards (never use `hover:` classes).
- All new business logic requires unit tests; use Testcontainers for integration tests and ArchUnit for architecture tests.
