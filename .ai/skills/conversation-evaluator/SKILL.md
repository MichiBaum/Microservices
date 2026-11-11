---
name: "conversation-evaluator"
description: "Evaluates and scores agent conversation logs in .agent-logs based on net utility, task completion, tool economy, and error resilience. Differential scoring ensures only unscored conversations are processed unless rescoring is requested."
---

# Conversation Evaluator Skill

This skill provides an empirical, utility-driven methodology for analyzing recorded agent sessions in `.agent-logs`. It quantifies the net value delivered by an agent while penalizing resource waste, repetitive loops, and unverified assumptions.

## Core Philosophy: Net Utility Maximization
An optimal agent interaction produces the maximum benefit (accurate problem resolution, clean code, compliant architecture) with the minimal cost (token consumption, redundant tool calls, runtime failures, user correction rounds).

$$\text{Net Utility} = \text{Benefit Delivered} - \text{Resource Waste} - \text{Friction Penalties}$$

---

## When to Use
- **Regular Audit**: Scoring newly completed sessions captured in `.agent-logs` to track agent efficiency over time.
- **Post-Task Assessment**: Evaluating a recently completed subagent or workflow session to measure performance.
- **Defect Investigation**: Auditing conversations that experienced failures, errors, or required user intervention.
- **Benchmark Rescoring**: Re-evaluating past conversations (`-Rescore`) following prompt enhancements or rubric calibrations.

---

## Differential Scoring Policy
To preserve computational resources and avoid redundant work:
1. **Unscored-by-Default**: The evaluator only scores conversations that do NOT already have a recorded evaluation in `.agent-logs/evaluations/ledger.json` and a corresponding `<sessionId>.json` record.
2. **Explicit Rescoring**: Historical conversations must only be rescored when the `-Rescore` flag is explicitly requested (e.g., when testing a calibrated rubric or updated evaluation heuristics).

---

## Utility Scoring Rubric (0 – 100)

| Dimension | Weight | Evaluation Criteria |
|:----------|:------:|:--------------------|
| **1. Task Resolution & Goal Achievement** | **35%** | • Verified terminal completion (`COMPLETED` state, final answer provided).<br>• Absence of user dissatisfaction or correction prompts.<br>• Full coverage of the user's effective issue without breaking secondary invariants. |
| **2. Tool Utility & Waste Minimization** | **25%** | • Low tool failure rate ($< 5\%$).<br>• Zero or minimal redundant/duplicate tool calls (no repetitive file reads or identical searches).<br>• Appropriate tool selection (e.g. targeted grep over exhaustive directory scans). |
| **3. Token Economy & Resource Overhead** | **15%** | • High signal-to-noise ratio in model prompts and responses.<br>• Token consumption proportional to task complexity ($< 150\text{k}$ for typical tasks).<br>• Streamlined turn count without wandering or speculative chatter. |
| **4. Error Resilience & Recovery** | **15%** | • Rapid autonomous recovery from unexpected tool failures.<br>• No infinite retry loops or repeated identical failing commands.<br>• Graceful fallback strategies when primary tools fail. |
| **5. Standards & Verification Compliance** | **10%** | • Explicit validation passes executed (e.g., `./mvnw test`, `npm test`, ArchUnit check).<br>• Adherence to project guidelines (MariaDB, mobile-first, clean code, Writerside docs). |

### Grade Classification
- **A+ (95–100)**: *Optimal Utility* — Flawless one-pass resolution, zero redundant tools, automated verification verified.
- **A (85–94)**: *High Utility* — Successful completion with minimal waste; minor non-blocking inefficiencies.
- **B (75–84)**: *Moderate Utility* — Goal achieved, but noticeable tool redundancy or moderate token overhead.
- **C (65–74)**: *Sub-optimal Utility* — Goal achieved with significant friction, multiple failed attempts, or missing verification.
- **D (50–64)**: *Marginal Utility* — Incomplete resolution, heavy failure loops, or repeated user corrections.
- **F (< 50)**: *Deficit / Wasteful* — Task abandoned, unhandled exceptions, or severe negative utility.

---

## Operational Workflow

### Step 1: Run the Automated Scoring Engine
Execute the PowerShell scoring engine to process conversation event streams:
```powershell
# Score only new, unscored conversations
.\scripts\score-agent-conversations.ps1

# Score up to 10 unscored conversations
.\scripts\score-agent-conversations.ps1 -Limit 10

# Rescore all conversations or a specific session
.\scripts\score-agent-conversations.ps1 -Rescore
.\scripts\score-agent-conversations.ps1 -SessionId "session-261111-101936-10qo" -Rescore
```

### Step 2: Review Ledger and Aggregates
Examine the evaluation results:
- Ledger: `.agent-logs/evaluations/ledger.json`
- Aggregate Summary: `.agent-logs/evaluations/summary.json`
- Optimization Report: `.agent-logs/evaluations/optimization-report.md`
- Individual Session Evaluations: `.agent-logs/evaluations/<sessionId>.json`

### Step 3: Deep Dive on Sub-optimal Sessions ($< 75$)
For any session scoring below threshold:
1. Open the session's `.agent-logs/evaluations/<sessionId>.json`.
2. Inspect `penalties`, `toolUsage`, and `agentRecommendations`.
3. Locate the offending turns in `.agent-logs/<sessionId>/events.jsonl`.
4. Determine whether the root cause was:
   - Ambiguous agent prompt/instructions in `.ai/agents/`.
   - Missing domain context in `.ai/skills/`.
   - Flawed tool usage pattern (e.g. invalid terminal escaping, bad regex).
   - Inaccurate evaluator calibration (evaluator penalty too strict or too lenient).

### Step 4: Dispatch to Self-Improvement
Pass the distilled findings to the `agent-self-improvement` skill to formulate and apply prompt and rule updates.
