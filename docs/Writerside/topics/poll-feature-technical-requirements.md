# Technical Requirements: Poll Feature
**Author/Context:** Software Entwicklung Baumberger
**Date:** November 2026

## 1. High-Level Conclusions & Architecture
The Poll feature is designed to be a highly flexible, open-ended survey tool. It prioritizes schema normalization, dynamic question types, and robust data integrity while intentionally deferring complex UI modularity and voter anti-fraud mechanisms for future iterations.

**Core Principles Established:**
* **Creator-Centric Results:** Results are strictly private and visible only to the creator. Participants never see results or "correctness" scores.
* **Separation of Concerns:** The structural template of a poll is strictly separated from participant submissions in the database.
* **Relational Storage:** Answer data is stored in normalized relational tables (row-per-value) rather than unstructured JSONB, ensuring data integrity and fast SQL aggregation.
* **Atomic Submissions:** Voting is handled as a single, atomic payload to prevent partial or corrupted datasets.

---

## 2. Detailed Technical Decisions (Q&A Log)

### A. Core Model & Container Hierarchy
* **Question:** Should we architect the core model around a multi-question container (`Poll` → 1..N `Question` → 1..N `Option`), treating a standard single-question poll simply as the N=1 default case?
* **Conclusion (YES):** Modeling polls as a container of questions gives maximum future flexibility with minimal added complexity on day one.
* **Question:** Should both `Question` and `QuestionOption` include an explicit `order_index` integer column?
* **Conclusion (YES):** Storing an explicit sort index ensures predictable UI rendering and smooth drag-and-drop builder support.

### B. Question Types & Configuration
* **Question:** Should we support a polymorphic question structure where each question specifies a `type` (e.g., `single_choice`, `multiple_choice`, `free_text`) and stores constraints in JSON metadata?
* **Conclusion (YES):** A typed question design with flexible JSON metadata keeps the schema clean while allowing the UI renderer and validation pipeline to be completely modular.
* **Question:** Should we formalize a user-expanded `multi_entry` question type (e.g., "What days are you free?"), configured via a settings object defining the item data type (`date`) and bounds?
* **Conclusion (YES):** Specifying the scalar subtype allows clean dynamic UI inputs (date pickers, tag inputs) and strict payload validation without distinct database tables.
* **Question:** Should each question support an `is_required` boolean along with type-specific validation rules (`min_selected`, `max_selected`, `min_items`)?
* **Conclusion (YES):** Question-level constraint rules provide strict data hygiene and empower the UI to generate client-side form validation dynamically.

### C. Submission & Storage Schema
* **Question:** Should we separate the Poll definition schema from the Submission data schema?
* **Conclusion (YES):** Separating template structure from response data keeps database models normalized and makes polymorphic answers clean to query.
* **Question:** Should submissions be handled as an all-or-nothing, atomic transaction?
* **Conclusion (YES):** Atomic batch submissions simplify backend validation, preserve response integrity, and avoid dirty partial datasets.
* **Question:** Instead of JSONB storage, should we model answers using a normalized row-per-entry table (`QuestionAnswerValue` with nullable `option_id`, `text_value`, `date_value`)?
* **Conclusion (YES):** A normalized row-per-value answer table provides strict relational integrity, simple indexing, and frictionless standard SQL aggregations.

### D. Data Integrity & Lifecycle
* **Question:** Should a poll's operational status be governed by an explicit state machine (`DRAFT` → `PUBLISHED` → `CLOSED`)?
* **Conclusion (YES):** A formal three-state lifecycle provides clean operational guards for the API.
* **Question:** Should the backend enforce schema immutability once a poll receives its first submission?
* **Conclusion (YES):** Freezing the question and option structure upon the first submission guarantees data integrity, preventing orphaned responses or skewed percentages.
* **Question:** Should public polls be addressed via human-friendly short slugs (e.g., `/p/a8k2m9`) rather than database UUIDs/IDs?
* **Conclusion (YES):** Short random slugs produce clean, shareable links and prevent enumeration attacks.
* **Question:** Should the poll definition include a configurable post-submission completion state (custom thank-you message and optional redirect URL)?
* **Conclusion (YES):** A customizable completion screen provides clear closure for participants since they are not shown any results.

### E. API & Result Aggregation
* **Question:** Should the backend provide a sanitized, stripped-down public "Take Poll" DTO for voting participants?
* **Conclusion (YES):** A dedicated read-model for voting ensures internal details (creator metadata, total votes) never leak to the public via network inspection.
* **Question:** Should the backend provide two distinct results endpoints: Aggregated Metrics and Raw Data Exports?
* **Conclusion (YES):** Providing both covers quick visual comprehension and deep analytical review.
* **Question:** Should the aggregated results endpoint compute tallies dynamically on the fly using standard SQL (`COUNT`, `GROUP BY`) rather than maintaining incremented counter tables?
* **Conclusion (YES):** Dynamic SQL calculation avoids write contention, eliminates synchronization bugs, and keeps the ingestion pipeline lightning-fast.
* **Question:** Should dynamic multi-entry values (like dates) be automatically normalized and bucketed into frequency tallies by the backend?
* **Conclusion (YES):** Pre-aggregating date frequencies on the backend makes multi-entry date questions immediately useful for scheduling availability.
* **Question:** For raw data exports (CSV), should multi-value questions be flattened into a single delimited cell per question (e.g., "2026-11-10; 2026-11-12")?
* **Conclusion (YES):** Keeping one row per submission with delimited multi-values preserves standard tabular formats for easy analysis in Excel/BI tools.

---

## 3. Explicitly Deferred & Out-of-Scope Items
The following concepts were discussed but intentionally postponed to keep the initial scope focused:

1. **Voter Identity & Anti-Cheating Policy:** Deferred. We will not build complex auth guards, IP tracking, or session token limits for voters in v1.
2. **Backend Idempotency Keys:** Deferred. We will rely on frontend request guards (disabling submit buttons) for v1 rather than building database-level duplicate constraints right now.
3. **Modular UI Architecture & Embeddability:** Deferred. Building headless components for third-party iframe embedding is out of scope for the current requirements phase.