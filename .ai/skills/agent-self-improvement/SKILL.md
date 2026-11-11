---
name: "agent-self-improvement"
description: "Iterative feedback and optimization system that converts conversation evaluation scores into concrete prompt and guideline improvements for the evaluator itself and domain agents across the project."
---

# Agent Self-Improvement & Closed-Loop Optimization Skill

This skill translates empirical conversation evaluations into targeted, high-utility improvements. By systematically closing the feedback loop, the system eliminates recurring agent inefficiencies, prevents token waste, and refines both evaluator accuracy and domain agent effectiveness.

---

## The Continuous Optimization Cycle

```
┌────────────────────────────────────────────────────────┐
│ 1. Collect & Score Sessions (.agent-logs)              │
│    - Compute Utility Scores, Waste, Error Penalties    │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│ 2. Analyze Patterns & Identify Leverage Points         │
│    - Dissect sessions scoring < 75 or high redundancy  │
└─────────────┬────────────────────────────┬─────────────┘
              │                            │
              ▼                            ▼
┌───────────────────────────┐┌───────────────────────────┐
│ 3A. Evaluator Calibration ││ 3B. Domain Agent Upgrades │
│  - Adjust rubric weights  ││  - Patch .ai/agents/*.md  │
│  - Refine heuristics      ││  - Enrich .ai/skills/     │
│  - Fix scoring biases     ││  - Update .ai/AGENTS.md   │
└─────────────┬─────────────┘└─────────────┬─────────────┘
              │                            │
              └─────────────┬──────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│ 4. Measure Utility Delta (Rescore or Next Run)         │
│    - Confirm increased scores, reduced token waste     │
└────────────────────────────────────────────────────────┘
```

---

## 1. Evaluator Self-Improvement Protocol

The evaluator must regularly calibrate its own metrics to ensure objective, high-fidelity assessment:

### A. Score Distribution Calibration
- **Inflation Prevention**: If $> 90\%$ of non-trivial coding sessions receive `A+`, scoring thresholds may be too lenient. Strengthen tool redundancy and verification penalties.
- **Harshness Mitigation**: If successful resolutions are receiving `D` or `F` due to normal iterative debugging, verify that autonomous recovery bonuses are properly rewarding agents that fix their own tool errors.

### B. Distinguishing Intentional Action from Redundant Waste
- **Chat vs Engineering**: Sessions consisting purely of conceptual discussion or architectural guidance must not be penalized for lacking tool calls or test executions.
- **Legitimate Tool Re-invocation**: Rerunning a command with modified parameters after an error is legitimate adaptation; re-executing identical commands with unchanged parameters is waste.

### C. Heuristic Tuning in `scripts/score-agent-conversations.ps1`
- When false penalties are detected (such as user question phrasing misinterpreted as a correction), refine regular expressions to only match true user reprimands on follow-up turns.
- Calibrate token threshold tiers according to evolving model context sizes and task complexity.
- **Deduplicate by `stepId`**: Junie emits several update events per logical tool call; one unique stepId = one call. Never count raw event lines, and never rely on terminal COMPLETED steps alone (file-operation tools complete implicitly).
- **Failure double-reporting**: Junie reports each task failure twice (top-level `AgentTaskFailedEvent` + wrapped `AgentFailureEvent`); distinct failures = max of both forms before applying the penalty.
- **No-data handling**: sessions without tool or token telemetry must be reported as no-data, never scored as free 100s or zero-token efficiency.

---

## 2. Upstream Domain Agent Optimization Protocol

Evaluation scores reveal concrete operational bottlenecks. Map detected deficiencies directly to the corresponding agent prompts and skills:

### Optimization Mapping Matrix

| Detected Pattern | Root Cause | Target Improvement |
|:-----------------|:-----------|:-------------------|
| **High Duplicate Tool Calls** | Agent re-reads files or repeats glob searches | Add strict memory caching instructions in `.ai/AGENTS.md` and `global-skills`: *"Never re-read an unchanged file already opened in context."* |
| **Terminal Syntax Errors** | Bad quotes or Bash syntax in PowerShell | Update terminal guidelines in `.ai/AGENTS.md` and `infrastructure-tooling`: specify PowerShell single-quoting `'-Dkey=value'` and doubled quotes. |
| **Missing Test Verification** | Agent claims completion without running `./mvnw test` | Patch target agent in `.ai/agents/` (e.g. `spring-microservices-architect.md`): add mandatory pre-completion test verification step. |
| **ArchUnit Architecture Violations** | Cross-module direct dependencies introduced | Update `testing-code-review-expert.md` and `quality-documentation-guardian.md` to require `./mvnw test -pl archunittests`. |
| **Outdated Documentation** | API changes without Writerside updates | Update `quality-documentation-guardian.md` and PR checklists to enforce synchronized documentation. |

---

## Step-by-Step Execution Guide

### Step 1: Ingest Evaluation Summary
Read `.agent-logs/evaluations/summary.json` and `.agent-logs/evaluations/optimization-report.md`. Note:
1. Aggregate tool failure rate and redundancy rate.
2. Underperforming sessions (lowest scores).
3. Systemic recommendations.

### Step 2: Formulate Targeted Edits
Formulate concise, actionable additions for target files. Follow these constraints:
- **Conciseness**: Keep instructions atomic. Do not bloat agent prompts with verbose essays.
- **Clarity**: State the exact rule, the rationale (waste prevention / error avoidance), and the correct idiom.
- **Non-contradiction**: Ensure new rules align with existing project conventions in `docs/Writerside/topics/Guidelines.md`.

### Step 3: Apply Updates to Agents and Skills
Edit the relevant files:
- Domain agents: `.ai/agents/<agent-name>.md`
- Skills: `.ai/skills/<skill-name>/SKILL.md`
- Global rules: `.ai/AGENTS.md` or `.ai/skills/global-skills/SKILL.md`

### Step 4: Verify Utility Improvement
On subsequent sessions, confirm that:
- Tool duplication drops toward 0.
- Command syntax errors disappear.
- Verification passes occur consistently before completion.
- Composite utility scores trend upward toward optimal levels ($> 90$).
