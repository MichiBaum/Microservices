---
name: "documentation-standards"
description: "Guidelines for technical writing, KDoc, API documentation, and project documentation management using JetBrains Writerside. Use when writing or updating documentation, adding KDoc to code, or documenting APIs."
---
# Documentation Standards

## When to Use
- Adding or updating project documentation in `docs/Writerside/topics/`.
- Writing KDoc for public APIs, classes, or functions.
- Documenting REST API endpoints.
- Updating changelogs or README files after user-facing changes.

## Writerside Documentation
- Project documentation is managed using **JetBrains Writerside** in `docs/`.
- All topic files are Markdown (`.md`) in `docs/Writerside/topics/`.
- Update documentation whenever you change public APIs, architecture, or user-facing behavior.
- Keep documentation concise, accurate, and structured with clear headings.

## KDoc Standards
- Add KDoc to all public classes, functions, and properties.
- Match the existing documentation frequency and style in the codebase.
- Use `@param`, `@return`, `@throws` tags for function documentation.
- Do not add trivial KDoc that merely restates the function name.

## REST API Documentation
- Document all REST endpoints with their HTTP method, path, request/response bodies, and status codes.
- Include example request and response payloads where helpful.
- Document authentication requirements for each endpoint.

## Changelog Guidelines
- Update the changelog for any user-facing change.
- Group changes under: Added, Changed, Fixed, Removed.
- Include the affected service or module name.

## Constraints
- Do not add comments that duplicate what the code already clearly expresses.
- Use the same language as existing comments in the codebase.
- Keep documentation up to date; outdated documentation is worse than no documentation.
