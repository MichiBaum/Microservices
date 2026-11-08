# Poll Service

The Poll Service is a microservice built with Kotlin and Spring Boot that provides survey creation, availability scheduling, and real-time response aggregation within the microservices ecosystem.

## Key Features

- **Hierarchical Survey Builder**: Organizes surveys into ordered sections, questions, and options.
- **Polymorphic Question Types**: Supports Single Select, Multiple Select, Free Text, Numerical Inputs, Date Pickers, and Multi-Entry Lists.
- **Validation Engine**: Declarative validation rules (lengths, regex, email/url format, numeric ranges, and date bounds).
- **Access Policies**: Supports `OPEN`, `INVITED_ONLY` (whitelisted users), and `SECRET_PROTECTED` (passcode) polls.
- **Secret Ballot & Anonymity**: Toggle between identified participant responses and confidential anonymous surveys.
- **Dynamic SQL Aggregation**: Real-time response metrics, averages, and automated date availability tallies calculated dynamically via MariaDB.
- **Raw Data Export**: Tabular CSV exports of response data with strict anonymity protections.

## Data Model & Hierarchy

The service manages several core entities:

- **Poll**: Container for survey settings, lifecycle state (`DRAFT`, `PUBLISHED`, `CLOSED`), expiration timestamps, and access policy.
- **Section**: Grouping container for logical questionnaire parts.
- **Question**: Individual questions with polymorphic type configuration and validator rules.
- **QuestionOption**: Selectable options for choice-based questions.
- **PollSubmission**: Participant response header linked to authenticated user identity.
- **QuestionAnswer**: Answers per question containing normalized `QuestionAnswerValue` rows.

## Integration with Other Services

- **Authentication Service & Library**: Enforces JWT platform authentication for creators and participants.
- **Gateway Service**: Routes external traffic via `poll.michibaum.*`.
- **Admin & Registry Services**: Service discovery via Netflix Eureka and health monitoring via Spring Boot Admin.

## Permissions

- `POLL_SERVICE`: Base permission to access the Poll Service application.
- `POLL_SERVICE_CREATE_POLL`: Authority required to create and manage new polls.

## Technologies

- Kotlin & Java 26
- Spring Boot & Spring Data JPA
- MariaDB & Flyway Migrations
- Spring Cloud Gateway (MVC) & Netflix Eureka
- OpenTelemetry & Prometheus Actuator Metrics
- Docker & Kubernetes
