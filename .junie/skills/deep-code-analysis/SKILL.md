---
name: "deep-code-analysis"
description: "Expert identification of logic errors, race conditions, concurrency issues, and clean code violations. Use when reviewing code for bugs, performing code audits, or analyzing complex logic for correctness."
---
# Deep Code Analysis

## When to Use
- Reviewing pull requests or code changes for subtle bugs.
- Auditing existing code for logic errors, race conditions, or performance issues.
- Analyzing complex business logic for correctness and edge cases.
- Enforcing clean code principles during refactoring.

## Bug Detection Checklist
1. **Null Safety**: Check for potential `NullPointerException` paths. In Kotlin, flag any use of `!!`.
2. **Race Conditions**: Look for shared mutable state accessed from multiple threads or coroutines without synchronization.
3. **Off-by-One Errors**: Verify loop bounds, pagination offsets, and range calculations.
4. **Resource Leaks**: Ensure streams, connections, and closeable resources are properly closed (use `use {}` in Kotlin).
5. **Error Handling**: Verify exceptions are caught at appropriate levels and not silently swallowed.

## Clean Code Principles
- **SOLID**: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion.
- **DRY**: Extract duplicated logic into shared functions or services.
- **KISS**: Prefer simple, readable solutions over clever or over-engineered ones.
- **YAGNI**: Do not add functionality until it is actually needed.

## Static Analysis
- Use **Qodana** for automated static analysis and code quality checks.
- Address all high-severity findings before merging.
- Configure Qodana rules in `qodana.yaml` at the project root.

## Code Review Output Format
When reporting issues, provide:
- File path and line number.
- Description of the issue and its potential impact.
- Suggested fix with code example.
- Severity level (critical, major, minor).
