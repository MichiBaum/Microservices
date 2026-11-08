# User Stories: Poll Service
**Author/Context:** Software Entwicklung Baumberger  
**Date:** November 2026

## 1. Personas

To maximize survey utility, ensure high response rates, and prevent data corruption, the Poll Service is designed around two primary user personas, both operating within the platform's authenticated ecosystem.

### A. Poll Creator (Organizer / Decision Maker)
* **Description:** An authenticated user who initiates surveys, polls, or scheduling inquiries to collect group input and make data-driven decisions.
* **Goals & Motivations:**
  * Fast, intuitive poll creation with customizable question types, descriptions, and selection bounds.
  * Setting optional expiration deadlines (`expires_at`) for automated lifecycle management.
  * Enforcing access control via `OPEN`, `INVITED_ONLY` (whitelisted users), or `SECRET_PROTECTED` (passcode) access policies.
  * Choosing between transparent identified responses (for scheduling) and confidential anonymous secret ballots (for feedback).
  * Managing active and archived surveys via a centralized Creator Dashboard.
  * Ensuring data hygiene through required fields and strict client/server validation.
  * Protecting survey integrity by locking poll definitions once voting begins.
  * Accessing private, real-time aggregated metrics and tabular raw data exports (CSV) without exposing results to respondents.
* **Pain Points to Avoid:** Incomplete datasets, ambiguous multi-choice selections, skewed results caused by mid-survey schema changes, manual deadline tracking, unauthorized responses, and scheduling tally overhead.

### B. Poll Participant (Voter / Respondent)
* **Description:** An authenticated user who accesses a shared poll to submit or revise their preferences, opinions, or availability.
* **Goals & Motivations:**
  * Frictionless access via short, clean, human-friendly URLs on any device.
  * Clear access prompts for secret-protected polls and clear indication of invitation status.
  * A clear, distraction-free interface presenting question prompts, rich markdown instructions, and clear bounds.
  * Fast, atomic submissions with clear validation feedback and zero risk of partial data loss.
  * Ability to revisit the poll and update their answers as long as the poll remains active and open.
  * Immediate confirmation upon completion with custom thank-you guidance or relevant redirects.
* **Pain Points to Avoid:** Cumbersome long links, inability to correct accidental submission mistakes or update scheduling availability, confusing form validation errors, and lost answers.

---

## 2. User Stories

The following user stories define the functional and behavioral scope of the Poll Service, organized by operational phase.

### A. Poll Authoring & Configuration

#### US-01: Sectioned Multi-Question Poll Container
* **Story:** As a **Poll Creator**, I want to organize a survey into 1..N structured sections containing multiple ordered questions with contextual descriptions, so that I can guide participants through logical multi-part or multi-page questionnaires.
* **Acceptance Criteria:**
  * A poll container supports 1 to N sections, and each section supports 1 to N questions.
  * Simple single-section quick polls function as the default N=1 case without structural divergence.
  * `Poll`, `Section`, and `Question` entities all support optional markdown/plain-text descriptions for context and instructions.

#### US-02: Explicit Multi-Tier Sequence & Ordering
* **Story:** As a **Poll Creator**, I want to define and adjust the visual sequence of sections, questions, and options using explicit order indexes, so that participants experience a logical, coherent flow that maximizes response rates.
* **Acceptance Criteria:**
  * `Section`, `Question`, and `QuestionOption` entities contain explicit `order_index` integer attributes.
  * Rendered UI and API outputs sort elements predictably by `order_index` at every hierarchy tier.

#### US-03: Single and Multiple Choice Configuration
* **Story:** As a **Poll Creator**, I want to configure single-choice and multiple-choice questions with minimum and maximum selection limits, so that I collect exact and well-bounded decision data without ambiguous selections.
* **Acceptance Criteria:**
  * `SINGLE_SELECT` questions enforce exactly one selection.
  * `MULTI_SELECT` questions validate selections against configurable `min_selected` and `max_selected` rules stored in metadata.

#### US-04: Text and Numerical Input with Format Validators
* **Story:** As a **Poll Creator**, I want to configure free-text and numerical questions with declarative validation rules (such as email format, string lengths, regex patterns, and numeric ranges), so that I ensure participants provide well-formed, valid answers.
* **Acceptance Criteria:**
  * `TEXT` questions support `min_length`, `max_length`, custom `pattern` (regex), and `format` presets (`email`, `url`, `phone`, `alphanumeric`).
  * `NUMERICAL` questions enforce `min_value`, `max_value`, and `integer_only` constraints, stored as structured numeric values for instant statistical analysis.

#### US-05: Date and Multi-Entry Availability Scheduling
* **Story:** As a **Poll Creator**, I want to configure single date and multi-entry date/tag fields with bounds and calendar restrictions, so that I can easily discover overlapping participant availability without manual date entry errors.
* **Acceptance Criteria:**
  * `DATE` questions enforce date bounds (`min_date`, `max_date`, `disable_past`).
  * `MULTI_ENTRY` questions accept dynamic list inputs with specified scalar subtypes (e.g., `date`) and validate against `min_items` and `max_items` constraints.

#### US-06: Mandatory Fields and Rejection Feedback
* **Story:** As a **Poll Creator**, I want to mark specific questions as mandatory and enforce strict client and server validation, so that I eliminate incomplete submissions and ensure high-integrity datasets.
* **Acceptance Criteria:**
  * Each question supports an `is_required` flag.
  * Submissions omitting required questions or violating any validator rule are rejected with precise, field-specific error messages.

#### US-07: Custom Completion and Redirection
* **Story:** As a **Poll Creator**, I want to customize the thank-you screen and optional redirect URL, so that participants receive immediate closure and can be guided to follow-up resources.
* **Acceptance Criteria:**
  * Poll settings include a custom thank-you message text.
  * An optional redirect URL is supported and triggered upon successful submission.

#### US-08: Configurable Access Policies & Secrets
* **Story:** As a **Poll Creator**, I want to configure the audience access policy as `OPEN`, `INVITED_ONLY`, or `SECRET_PROTECTED`, so that I control precisely who can access and participate in my poll.
* **Acceptance Criteria:**
  * `OPEN` allows any authenticated platform user with the slug to vote.
  * `INVITED_ONLY` restricts participation to a creator-defined list of whitelisted `user_id`s.
  * `SECRET_PROTECTED` requires the participant to supply a matching passcode before taking or submitting the poll.

#### US-09: Configurable Anonymity & Secret Ballots
* **Story:** As a **Poll Creator**, I want to toggle between identified and anonymous participation modes, so that I can see names for scheduling polls while guaranteeing strict confidentiality for feedback surveys.
* **Acceptance Criteria:**
  * Setting `is_anonymous = false` links respondent usernames to responses in raw data views and exports.
  * Setting `is_anonymous = true` strips user identifiers from response records, preventing anyone (including the creator) from correlating answers to individuals.

---

### B. Lifecycle & Data Integrity

#### US-10: Lifecycle State & Deadline Management
* **Story:** As a **Poll Creator**, I want to transition the poll across `DRAFT`, `PUBLISHED`, and `CLOSED` states and optionally set an expiration timestamp (`expires_at`), so that I prevent premature voting during setup and automatically or manually terminate data collection when finished.
* **Acceptance Criteria:**
  * Voting is only permitted when the poll is in `PUBLISHED` state and `now() <= expires_at` (if set).
  * Polls in `DRAFT` remain editable and inaccessible to participants.
  * Polls past expiration or set to `CLOSED` reject new submissions while keeping historical results accessible to the creator.

#### US-11: Schema Immutability Guarantee
* **Story:** As a **Poll Creator**, I want the system to automatically freeze the poll's section, question, and option structure once the first response is submitted, so that existing responses remain consistent and statistical integrity is strictly preserved.
* **Acceptance Criteria:**
  * Section, question, and option additions, deletions, or structural modifications are blocked after the first submission is recorded.
  * Prevents orphaned answers and corrupted aggregation percentages.

#### US-12: Creator Dashboard & Poll Management
* **Story:** As a **Poll Creator**, I want a centralized dashboard to list, filter, paginate, and soft-delete my polls, so that I can easily monitor and manage all my surveys from one place.
* **Acceptance Criteria:**
  * Creator dashboard API lists all polls created by the authenticated user with response counts and status.
  * Supports status filtering (`DRAFT`, `PUBLISHED`, `CLOSED`) and pagination.
  * Soft-delete safely marks polls as deleted without corrupting database integrity.

#### US-13: Poll Duplication & Template Cloning
* **Story:** As a **Poll Creator**, I want to duplicate an existing poll into a fresh draft, so that I can quickly spin up recurring surveys and availability polls without rebuilding sections, questions, and options from scratch.
* **Acceptance Criteria:**
  * Cloning an existing poll copies all sections, questions, options, constraints, and descriptions into a new `DRAFT` state poll.
  * Generates a brand-new unique short slug and zeroes out all submission counts and response data.

---

### C. Participation & Voting Flow

#### US-14: Frictionless Access & Invitation Discovery
* **Story:** As a **Poll Participant**, I want to access polls through short URLs or discover all polls I have been directly invited to via an invitations dashboard, so that I never miss an important survey or scheduling deadline.
* **Acceptance Criteria:**
  * Accessing via clean short slug routes directly to the poll.
  * An invitations dashboard (`GET /api/v1/participant/invitations`) lists active, open polls where the authenticated user is on the invite whitelist.
  * If the poll is `INVITED_ONLY` and the user is not whitelisted, an explicit access denied error is returned.

#### US-15: Stateless Passcode Entry for Secret-Protected Polls
* **Story:** As a **Poll Participant**, I want to supply a secret passcode to unlock a protected poll without maintaining sticky browser sessions, so that I can easily vote across different tabs or devices.
* **Acceptance Criteria:**
  * Protected polls validate the passcode statelessly via `X-Poll-Secret` header or request body.
  * Supplying the correct passcode provides the sanitized section and question DTO and authorizes the response submission.

#### US-16: Sanitized Voting Form & Submission Pre-filling
* **Story:** As a **Poll Participant**, I want to view a clean voting interface organized by sections with clear question prompts, validator guidelines, and my previous answers if already submitted, so that I have clear context and can easily review my current responses.
* **Acceptance Criteria:**
  * The "Take Poll" DTO excludes creator IDs, total vote counts, and internal metadata.
  * UI renders sections and questions strictly by their respective `order_index`.
  * If the authenticated participant previously voted, their existing answers are loaded into the form for review and editing.

#### US-17: Atomic Submission & Response Re-voting
* **Story:** As a **Poll Participant**, I want to submit or update my answers as a single atomic transaction with dynamic validation feedback while the poll is active, so that my choices are accurately recorded and I can adjust my availability if my plans change.
* **Acceptance Criteria:**
  * All question responses across all sections for a poll are submitted in a single payload associated with the authenticated `user_id`.
  * If any validation constraint (e.g. email format, numeric bounds) fails, the entire transaction is rolled back.
  * If the user previously submitted, their answer set is atomically updated in the database.
  * Submissions are rejected if the poll is closed or expired.

#### US-18: Submission Confirmation
* **Story:** As a **Poll Participant**, I want to receive immediate visual confirmation after submitting my answers, so that I am confident my contribution was successfully recorded without leaking sensitive voting results.
* **Acceptance Criteria:**
  * Displays the configured thank-you message or executes the redirect URL.
  * Aggregate poll results, vote counts, and other participants' submissions remain strictly hidden from participants.

---

### D. Results Analytics & Export

#### US-19: Private Real-Time Metrics & Numerical Statistics
* **Story:** As a **Poll Creator**, I want to view private, real-time aggregated metrics (including frequency tallies for choices and summary statistics like averages/ranges for numerical questions), so that I can make fast, evidence-based decisions without exposing sensitive data to participants.
* **Acceptance Criteria:**
  * Results endpoints are protected and accessible only to the poll creator.
  * Tallies and numerical summaries (e.g., average, min, max, count) are computed dynamically on the fly via SQL aggregations.

#### US-20: Automated Date Availability Bucketing
* **Story:** As a **Poll Creator**, I want the system to automatically aggregate and rank multi-entry date availability into frequency totals, so that I can instantly identify the optimal date that maximizes group consensus.
* **Acceptance Criteria:**
  * Multi-entry date responses are automatically grouped and tallied by date value.
  * Aggregation output surfaces frequency distributions directly for scheduling consensus.

#### US-21: Tabular CSV Data Export with Anonymity Enforcement
* **Story:** As a **Poll Creator**, I want to export raw, normalized response data as a clean CSV with delimited multi-value fields and proper anonymity redaction, so that I can perform deep offline analysis while honoring participant confidentiality.
* **Acceptance Criteria:**
  * Export generates one row per submission.
  * Participant username/ID is populated for non-anonymous polls and omitted/redacted for `is_anonymous = true` polls.
  * Multi-value responses (such as multiple choice or multi-entry dates) are formatted as delimited values within their respective question column.
