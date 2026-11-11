---
name: database-expert
description: Expert in MariaDB, Flyway migrations, and JPA/Hibernate best practices for the Microservices project
---

# Database Expert

Use this skill when working with MariaDB database, Flyway migrations, or optimizing database queries in the Microservices project.

## When to Use

- Creating new database schemas or migrations
- Optimizing complex SQL queries
- Configuring Flyway migration management
- Troubleshooting JPA/Hibernate issues
- Designing database schemas for new services
- Reviewing Flyway migration files

## Database Standards

### MariaDB
- Use MariaDB as the primary database for all services
- Each service owns its database schema exclusively (no shared tables)
- Use connection pooling (HikariCP) for all database connections
- Configure connection limits and timeouts appropriately

### Flyway Migrations
- All schema changes must be in `src/main/resources/db/migration/`
- Use naming convention: `V<version>+<description>.sql`
- Migrations must be idempotent and reversible where possible
- Test migrations independently before applying to production

## Flyway Migration Rules

### Naming Convention
```
V001.0.0__initial_schema.sql
V002.0.0__add_user_table.sql
V003.0.0__create_indexes.sql
```

### Best Practices
- Start with version `001.0.0`
- Use descriptive names after the `__` delimiter
- Keep migrations focused and atomic
- Add comments for complex migrations
- Never modify existing tables without a migration
- Always include rollback scripts if needed

## JPA/Hibernate Best Practices

### Entity Design
- Use `@Entity` for all database tables
- Use `@Id` with `@GeneratedValue` for primary keys
- Use `@Column` for column-level configuration
- Avoid entity naming conflicts between services

### Query Optimization
- Use `@Query` with JPQL for complex queries
- Avoid N+1 queries by using JOIN FETCH when needed
- Use `@Index` for frequently queried columns
- Enable SQL logging for query debugging

### Relationships
- Use `@ManyToOne`, `@OneToMany`, `@ManyToMany` appropriately
- Define cascade rules explicitly
- Use `@JoinColumn` for foreign key configuration
- Avoid circular references without proper handling

## Migration Workflow

1. **Create Migration**: Add SQL to `src/main/resources/db/migration/`
2. **Test Locally**: Run `./mvnw flyway:migrate -pl <service>`
3. **Verify**: Check that schema changes applied correctly
4. **Commit**: Commit migration file with clear commit message
5. **Deploy**: Migrations apply automatically on service startup

## Common Tasks

### Creating a New Table
```sql
-- V004.0.0__create_order_table.sql
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
);
```

### Adding an Index
```sql
-- V005.0.0__add_user_email_index.sql
CREATE INDEX `idx_user_email` ON `user` (`email`);
```

### Altering a Table
```sql
-- V006.0.0__add_order_total_column.sql
ALTER TABLE `order`
ADD COLUMN `total_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0.00;
```

## Output Standards

- Provide complete Flyway migration file content
- Include both forward and backward migration scripts if needed
- Specify exact file path for the migration
- Include validation steps to verify the migration

## Project-Specific Notes

- All services use MariaDB as the database
- Flyway is configured via Spring Boot auto-configuration
- Database names follow service naming convention (e.g., `chess-service`)
- Use Flyway callbacks for pre/post migration hooks