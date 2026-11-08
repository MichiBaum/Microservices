# Technical Requirements: Poll Feature
**Author/Context:** Software Entwicklung Baumberger
**Date:** November 2026

## 1. High-Level Conclusions & Architecture
The Poll feature is designed to be a flexible, authenticated survey and scheduling tool. It prioritizes schema normalization, dynamic question types, and robust data integrity while integrating natively with platform identity and authorization.

**Core Principles Established:**
* **Authenticated Access & Multi-Tenancy:** Both poll creation and participation are authenticated via platform JWT (`authentication-library`). Submissions are linked to the participant's user ID.
* **Hierarchical Sectioned Surveys:** Polls are structured into 1..N `Section` containers, each containing 1..N `Question` items, enabling logical multi-page or multi-part questionnaires.
* **Rich Type & Validator Engine:** Support for polymorphic question types (`SINGLE_SELECT`, `MULTI_SELECT`, `TEXT`, `NUMERICAL`, `DATE`, `MULTI_ENTRY`) with declarative validator configurations (e.g., regex patterns, email/URL semantic formats, numeric bounds, and date restrictions).
* **Granular Access Control:** Polls support three configurable access policies: `OPEN` (any platform user), `INVITED_ONLY` (whitelisted user IDs), and `SECRET_PROTECTED` (poll-specific passcode).
* **Configurable Anonymity (Secret Ballot):** Creators can toggle `is_anonymous` per poll. When enabled, participant user IDs are dissociated from answer rows in creator results and exports.
* **Creator-Centric Results:** Results are strictly private and visible only to the poll creator. Participants never see aggregated results or other users' votes.
* **Separation of Concerns:** The structural template of a poll is strictly separated from participant submissions in the database.
* **Relational Storage:** Answer data is stored in normalized relational tables (row-per-value with `text_value`, `numeric_value`, `date_value`, `option_id`), ensuring data integrity and fast SQL aggregation.
* **Atomic Submissions & Re-voting:** Voting is handled as a single, atomic payload. Authenticated participants can revisit and update their submitted response while the poll remains active.
* **Temporal Lifecycle & Auto-Closing:** Polls support an optional `expires_at` deadline alongside explicit lifecycle states (`DRAFT` → `PUBLISHED` → `CLOSED`).
* **Creator Management:** Centralized dashboard API for creators to list, filter, paginate, soft-delete, and duplicate polls.

---

## 2. Detailed Technical Decisions (Q&A Log)

### A. Core Model & Container Hierarchy
* **Question:** Should we architect the core model around a 3-tier hierarchy (`Poll` → 1..N `Section` → 1..N `Question` → 1..N `QuestionOption`), treating a single-section poll simply as the N=1 default case?
* **Conclusion (YES):** Structuring surveys into sections allows multi-topic or multi-page questionnaires while keeping single-section quick polls simple as the default N=1 case.
* **Question:** Should `Section`, `Question`, and `QuestionOption` all include an explicit `order_index` integer column?
* **Conclusion (YES):** Storing an explicit sort index at all three levels ensures predictable UI rendering and smooth drag-and-drop builder support.
* **Question:** Should `Poll`, `Section`, and `Question` entities support optional `description` fields for rich context/instructions?
* **Conclusion (YES):** Adding optional markdown/plain-text descriptions at container, section, and question levels provides comprehensive guidance to participants.

### B. Question Types & Validator Engine
* **Question:** What polymorphic question types should the engine support?
* **Conclusion (STANDARDIZED TYPE MATRIX):** The engine supports 6 core types:
  1. `SINGLE_SELECT`: Exactly 1 option chosen from a list of predefined options.
  2. `MULTI_SELECT`: 1 to N options chosen from predefined options, governed by selection limits.
  3. `TEXT`: Free-form single-line or multi-line text input.
  4. `NUMERICAL`: Discrete or continuous numeric inputs stored in a dedicated numeric column.
  5. `DATE`: Single date pickers.
  6. `MULTI_ENTRY`: User-expanded dynamic list of items (e.g. multiple dates for availability scheduling).
* **Question:** How should question validators be configured and enforced?
* **Conclusion (DECLARATIVE VALIDATOR JSON):** Each question defines an `is_required` flag and optional type-specific validation rules:
  * **`TEXT` Validators:** `min_length`, `max_length`, `pattern` (regex), and `format` preset (`email`, `url`, `phone`, `alphanumeric`).
  * **`NUMERICAL` Validators:** `min_value`, `max_value`, `integer_only` (stored as `numeric_value` in the DB for SQL averages/sums).
  * **`DATE` Validators:** `min_date`, `max_date`, `disable_past`.
  * **`MULTI_SELECT` & `MULTI_ENTRY` Validators:** `min_selected`/`min_items`, `max_selected`/`max_items`.
* **Question:** Are validators enforced on both client and server?
* **Conclusion (YES):** Dynamic validator metadata allows the frontend to render instant client-side validation hints while the backend strictly re-validates the complete submission payload.

### C. Submission & Storage Schema
* **Question:** Should we separate the Poll definition schema from the Submission data schema?
* **Conclusion (YES):** Separating template structure from response data keeps database models normalized and makes polymorphic answers clean to query.
* **Question:** Should submissions be handled as an all-or-nothing, atomic transaction tied to the voter's authenticated identity?
* **Conclusion (YES):** Atomic batch submissions simplify backend validation, preserve response integrity, and associate responses with the authenticated participant (`user_id`).
* **Question:** Can authenticated participants revise their submitted answers while the poll is active?
* **Conclusion (YES):** When an authenticated user submits answers to a poll they previously answered, the system atomically updates their existing submission record as long as the poll is `PUBLISHED` and not expired.
* **Question:** Instead of JSONB storage, should we model answers using a normalized row-per-entry table (`QuestionAnswerValue` with nullable `option_id`, `text_value`, `numeric_value`, `date_value`)?
* **Conclusion (YES):** A normalized row-per-value answer table provides strict relational integrity, simple indexing, and frictionless standard SQL aggregations (including averages on `numeric_value` and frequencies on `date_value`).

### D. Data Integrity, Access Control & Lifecycle
* **Question:** Should a poll's operational status be governed by an explicit state machine (`DRAFT` → `PUBLISHED` → `CLOSED`) and optional expiration?
* **Conclusion (YES):** A formal three-state lifecycle combined with an optional `expires_at` timestamp provides clean operational guards and automated deadline handling.
* **Question:** How should audience participation access be controlled?
* **Conclusion (CONFIGURABLE ACCESS POLICY):** Polls support three access modes configured on creation/edit:
  1. `OPEN`: Any authenticated platform user with the link can participate.
  2. `INVITED_ONLY`: Only explicitly whitelisted `user_id`s in a join table can participate.
  3. `SECRET_PROTECTED`: Participants must provide a matching poll secret/passcode before accessing or submitting the poll.
* **Question:** How should passcode verification be handled for `SECRET_PROTECTED` polls?
* **Conclusion (STATELESS VERIFICATION):** The passcode is passed statelessly via request header (`X-Poll-Secret`) or request payload during both poll retrieval and response submission, eliminating session state overhead.
* **Question:** Should invited participants have an API endpoint to discover polls they were invited to?
* **Conclusion (YES):** `GET /api/v1/participant/invitations` returns all active, open polls where the authenticated user's ID is on the invite whitelist, sorted by closing date.
* **Question:** Should creators be able to duplicate / clone existing polls?
* **Conclusion (YES):** `POST /api/v1/creator/polls/{id}/duplicate` clones the complete question, option, and constraint structure into a new `DRAFT` with a fresh slug and zero submissions.
* **Question:** Should creators be able to choose whether a poll is anonymous or identified?
* **Conclusion (YES):** Supporting an `is_anonymous` boolean per poll allows creators to conduct both transparent scheduling polls (where knowing who chose what is essential) and confidential surveys (where voter identities are stripped from results).
* **Question:** Should the backend enforce schema immutability once a poll receives its first submission?
* **Conclusion (YES):** Freezing the question and option structure upon the first submission guarantees data integrity, preventing orphaned responses or skewed percentages.
* **Question:** Should public polls be addressed via human-friendly short slugs (e.g., `/p/a8k2m9`) rather than database UUIDs/IDs?
* **Conclusion (YES):** Short random slugs produce clean, shareable links and prevent enumeration attacks.
* **Question:** Should the poll definition include a configurable post-submission completion state (custom thank-you message and optional redirect URL)?
* **Conclusion (YES):** A customizable completion screen provides clear closure for participants since they are not shown any results.

### E. API & Result Aggregation
* **Question:** Should the backend provide a sanitized, stripped-down public "Take Poll" DTO for voting participants?
* **Conclusion (YES):** A dedicated read-model for voting ensures internal details (creator metadata, total votes) never leak to the public via network inspection, while providing the user's previously submitted responses for easy editing if already answered.
* **Question:** Should the service provide a dedicated Creator Dashboard API?
* **Conclusion (YES):** `GET /api/v1/creator/polls` (with status filtering, search, and pagination) and `DELETE /api/v1/creator/polls/{id}` (soft-delete) provide complete poll management for creators.
* **Question:** Should the backend provide two distinct results endpoints: Aggregated Metrics and Raw Data Exports?
* **Conclusion (YES):** Providing both covers quick visual comprehension and deep analytical review, with participant IDs included or omitted based on the poll's `is_anonymous` setting.
* **Question:** Should the aggregated results endpoint compute tallies dynamically on the fly using standard SQL (`COUNT`, `GROUP BY`) rather than maintaining incremented counter tables?
* **Conclusion (YES):** Dynamic SQL calculation avoids write contention, eliminates synchronization bugs, and keeps the ingestion pipeline lightning-fast.
* **Question:** Should dynamic multi-entry values (like dates) be automatically normalized and bucketed into frequency tallies by the backend?
* **Conclusion (YES):** Pre-aggregating date frequencies on the backend makes multi-entry date questions immediately useful for scheduling availability.
* **Question:** For raw data exports (CSV), should multi-value questions be flattened into a single delimited cell per question (e.g., "2026-11-10; 2026-11-12")?
* **Conclusion (YES):** Keeping one row per submission with delimited multi-values preserves standard tabular formats for easy analysis in Excel/BI tools.

---

## 3. Explicitly Deferred & Out-of-Scope Items
The following concepts were discussed but intentionally postponed to keep the initial scope focused:

1. **Domain Events & Event-Driven Subscriptions:** Deferred. For v1, the service operates as a synchronous REST microservice without Kafka/RabbitMQ domain event emission until external subscriber needs are identified.
2. **Backend Idempotency Keys:** Deferred. We will rely on unique constraint per `(poll_id, user_id)` and frontend request guards for v1.
3. **Modular UI Architecture & Embeddability:** Deferred. Building headless components for third-party iframe embedding is out of scope for the current requirements phase.