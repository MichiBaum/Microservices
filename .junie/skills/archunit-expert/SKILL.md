---
name: "archunit-expert"
description: "Enforcement of architectural constraints, modularity, and dependency rules using ArchUnit. Use when adding architectural tests, verifying module boundaries, or checking that new code follows project structure rules."
---
# ArchUnit Expert

## When to Use
- Adding new architectural constraint tests.
- Verifying that new services or libraries follow module boundary rules.
- Checking for dependency cycles or forbidden dependencies.
- Reviewing code changes that might violate architectural patterns.

## Project Architecture Tests
- All ArchUnit tests live in the `archunittests/` module.
- Tests verify module boundaries, naming conventions, and dependency rules across the entire project.
- Run architecture tests: `./mvnw test -pl archunittests`

## Common Architectural Rules
- **No Circular Dependencies**: Services and libraries must not have circular Maven dependencies.
- **Layer Separation**: Controllers must not directly access repositories; use services as intermediaries.
- **Naming Enforcement**: Classes in `controller` packages must end with `Controller`; classes in `service` packages must end with `Service`.
- **Package Structure**: Each service must follow the standard package layout (`controller`, `service`, `repository`, `model`, `config`).

## Writing ArchUnit Tests
1. Create test classes in `archunittests/src/test/kotlin/`.
2. Use `@AnalyzeClasses` to specify the packages to scan.
3. Define rules using ArchUnit's fluent API (`classes().that()...should()...`).
4. Use `ArchRule` fields annotated with `@ArchTest` for automatic execution.

## Step-by-Step: Adding a New Rule
1. Identify the architectural constraint to enforce.
2. Create or update a test class in `archunittests/`.
3. Write the rule using ArchUnit's DSL.
4. Run `./mvnw test -pl archunittests` to verify the rule passes.
5. If existing code violates the rule, document exceptions or fix the violations.
