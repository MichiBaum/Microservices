# User Stories: Poll Service
**Author/Context:** Software Entwicklung Baumberger  
**Date:** November 2026

## 1. Personas

To maximize survey utility, ensure high response rates, and prevent data corruption, the Poll Service is designed around two primary user personas.

### A. Poll Creator (Organizer / Decision Maker)
* **Description:** An authenticated user who initiates surveys, polls, or scheduling inquiries to collect group input and make data-driven decisions.
* **Goals & Motivations:**
  * Fast, intuitive poll creation with customizable question types and selection bounds.
  * Ensuring data hygiene through required fields and strict client/server validation.
  * Protecting survey integrity by locking poll definitions once voting begins.
  * Accessing private, real-time aggregated metrics and tabular raw data exports (CSV) without exposing results to respondents.
* **Pain Points to Avoid:** Incomplete datasets, ambiguous multi-choice selections, skewed results caused by mid-survey schema changes, and manual scheduling tally overhead.

### B. Poll Participant (Voter / Respondent)
* **Description:** An invited or public individual who accesses a poll to submit their preferences, opinions, or availability.
* **Goals & Motivations:**
  * Frictionless access via short, clean, human-friendly URLs on any device.
  * A clear, distraction-free interface presenting only the necessary question prompts.
  * Fast, atomic submissions with clear validation feedback and zero risk of partial data loss.
  * Immediate confirmation upon completion with custom thank-you guidance or relevant redirects.
* **Pain Points to Avoid:** Cumbersome long links, confusing form validation errors, loss of answers due to network drops during submission, and unclear completion status.

---

## 2. User Stories

The following user stories define the functional and behavioral scope of the Poll Service, organized by operational phase.

### A. Poll Authoring & Configuration

#### US-01: Multi-Question Poll Container
* **Story:** As a **Poll Creator**, I want to create a survey containing multiple ordered questions of various types, so that I can collect comprehensive, structured feedback in a single survey rather than managing multiple disconnected forms.
* **Acceptance Criteria:**
  * A single poll container supports 1 to N questions.
  * Single-question quick polls function as the default N=1 case without structural divergence.

#### US-02: Explicit Sequence & Ordering
* **Story:** As a **Poll Creator**, I want to define and adjust the visual sequence of questions and options using explicit order indexes, so that participants experience a logical, coherent flow that maximizes response rates.
* **Acceptance Criteria:**
  * `Question` and `QuestionOption` entities contain explicit `order_index` attributes.
  * Rendered UI and API outputs sort elements predictably by `order_index`.

#### US-03: Single and Multiple Choice Configuration
* **Story:** As a **Poll Creator**, I want to configure single-choice and multiple-choice questions with minimum and maximum selection limits, so that I collect exact and well-bounded decision data without ambiguous selections.
* **Acceptance Criteria:**
  * Single-choice questions enforce exactly one selection.
  * Multiple-choice questions validate selections against configurable `min_selected` and `max_selected` rules stored in metadata.

#### US-04: Multi-Entry Availability Scheduling
* **Story:** As a **Poll Creator**, I want to configure multi-entry date and tag fields, so that I can easily discover overlapping participant availability without manual date entry errors.
* **Acceptance Criteria:**
  * `multi_entry` question type accepts dynamic list inputs with specified scalar subtypes (e.g., `date`).
  * Bounded entries validate against `min_items` and `max_items` constraints.

#### US-05: Validation Constraints and Required Fields
* **Story:** As a **Poll Creator**, I want to mark specific questions as mandatory and set constraint rules, so that I eliminate incomplete submissions and ensure high-integrity datasets.
* **Acceptance Criteria:**
  * Each question supports an `is_required` flag.
  * Submissions omitting required questions or violating type constraints are rejected with precise validation errors.

#### US-06: Custom Completion and Redirection
* **Story:** As a **Poll Creator**, I want to customize the thank-you screen and optional redirect URL, so that participants receive immediate closure and can be guided to follow-up resources.
* **Acceptance Criteria:**
  * Poll settings include a custom thank-you message text.
  * An optional redirect URL is supported and triggered upon successful submission.

---

### B. Lifecycle & Data Integrity

#### US-07: Lifecycle State Management
* **Story:** As a **Poll Creator**, I want to transition the poll across `DRAFT`, `PUBLISHED`, and `CLOSED` states, so that I prevent premature voting during setup and cleanly terminate data collection when finished.
* **Acceptance Criteria:**
  * Voting is only permitted when the poll is in `PUBLISHED` state.
  * Polls in `DRAFT` remain editable and inaccessible to participants.
  * Polls in `CLOSED` state reject new submissions while keeping historical results accessible to the creator.

#### US-08: Schema Immutability Guarantee
* **Story:** As a **Poll Creator**, I want the system to automatically freeze the poll's question and option structure once the first response is submitted, so that existing responses remain consistent and statistical integrity is strictly preserved.
* **Acceptance Criteria:**
  * Question and option additions, deletions, or structural modifications are blocked after the first submission is recorded.
  * Prevents orphaned answers and corrupted aggregation percentages.

---

### C. Participation & Voting Flow

#### US-09: Frictionless Short URL Access
* **Story:** As a **Poll Participant**, I want to access polls through short, human-friendly URLs, so that I can quickly reach and complete the poll on any device without handling cumbersome IDs.
* **Acceptance Criteria:**
  * Public poll access is routed via clean, random alphanumeric slugs (e.g., `/p/a8k2m9`).
  * Direct access prevents enumeration attacks and keeps links easily shareable.

#### US-10: Sanitized and Private Voting Form
* **Story:** As a **Poll Participant**, I want to view a streamlined voting interface that only shows relevant question prompts, so that I can focus entirely on providing my answers without distraction or exposure of creator metadata.
* **Acceptance Criteria:**
  * The public "Take Poll" DTO excludes creator IDs, total vote counts, and internal status information.
  * UI renders questions and options strictly based on their `order_index`.

#### US-11: Atomic and Validated Submission
* **Story:** As a **Poll Participant**, I want my responses to be validated dynamically and submitted as a single atomic transaction, so that my choices are never partially lost or corrupted by network interruptions.
* **Acceptance Criteria:**
  * All question responses for a poll are submitted in a single payload.
  * If any validation rule fails, the entire transaction rolls back and returns actionable feedback.

#### US-12: Submission Confirmation
* **Story:** As a **Poll Participant**, I want to receive immediate visual confirmation after submitting my answers, so that I am confident my contribution was successfully recorded without leaking sensitive voting results.
* **Acceptance Criteria:**
  * Displays the configured thank-you message or executes the redirect URL.
  * Aggregate poll results, vote counts, and correctness indicators remain strictly hidden from participants.

---

### D. Results Analytics & Export

#### US-13: Private Real-Time Metrics
* **Story:** As a **Poll Creator**, I want to view private, real-time aggregated metrics and frequency tallies calculated directly from submitted responses, so that I can make fast, evidence-based decisions without exposing sensitive data to participants.
* **Acceptance Criteria:**
  * Results endpoints are protected and accessible only to the poll creator.
  * Frequency tallies are computed dynamically on the fly via SQL aggregations.

#### US-14: Automated Date Availability Bucketing
* **Story:** As a **Poll Creator**, I want the system to automatically aggregate and rank multi-entry date availability into frequency totals, so that I can instantly identify the optimal date that maximizes group consensus.
* **Acceptance Criteria:**
  * Multi-entry date responses are automatically grouped and tallied by date value.
  * Aggregation output surfaces frequency distributions directly for scheduling consensus.

#### US-15: Tabular CSV Data Export
* **Story:** As a **Poll Creator**, I want to export raw, normalized response data as a clean CSV with delimited multi-value fields, so that I can perform deep offline analysis and reporting in external spreadsheet or BI tools.
* **Acceptance Criteria:**
  * Export generates one row per submission.
  * Multi-value responses (such as multiple choice or multi-entry dates) are formatted as delimited values within their respective question column.
