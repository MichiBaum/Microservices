---
name: "persistence-optimization"
description: "Optimization of MariaDB queries, Flyway migration management, and JPA/Hibernate best practices. Use when creating database schemas, writing migrations, optimizing queries, or configuring caching."
---
# Persistence Optimization

## When to Use
- Creating or modifying database tables and schemas.
- Writing or updating Flyway migration scripts.
- Optimizing slow queries or adding indexes.
- Configuring JPA entities, repositories, or caching.

## Database Standards
- **MariaDB** is the primary relational database for all services.
- Each service owns its own database schema. No cross-service database access.

## Flyway Migrations
- All schema changes must go through Flyway migration scripts.
- Location: `src/main/resources/db/migration` within each service.
- Naming: `V<version>__<description>.sql` (e.g., `V1__create_users_table.sql`).
- Migrations are immutable once applied. Use new migration files for changes.
- Always test migrations against an empty database and an existing database.

## JPA / Hibernate Best Practices
- Use `@Entity` with explicit `@Table(name = "...")` annotations.
- Prefer `FetchType.LAZY` for associations; use `JOIN FETCH` in queries when eager loading is needed.
- Avoid N+1 query problems by using `@EntityGraph` or explicit fetch joins.
- Use Spring Data JPA repositories with custom `@Query` methods for complex queries.
- Use `@Version` for optimistic locking where concurrent updates are possible.

## Performance Optimization
- Add database indexes for columns used in `WHERE`, `JOIN`, and `ORDER BY` clauses.
- Use pagination (`Pageable`) for list endpoints returning large datasets.
- Leverage `spring-boot-starter-cache` for frequently read, rarely changed data.
- Monitor query performance using Spring Boot Actuator metrics.

## Constraints
- Never write raw SQL in service code; use repositories or named queries.
- Never modify an existing Flyway migration that has been applied.
- Never store binary blobs directly in the database without justification.
