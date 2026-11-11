---
name: score-conversations
description: Score unscored conversations in .agent-logs and generate agent performance optimization reports.
---
Run `.\scripts\score-agent-conversations.ps1` from the project root.
- To score only new/unscored sessions: `.\scripts\score-agent-conversations.ps1`
- To rescore previously evaluated conversations: `.\scripts\score-agent-conversations.ps1 -Rescore`
- To evaluate a specific session: `.\scripts\score-agent-conversations.ps1 -SessionId "<sessionId>"`
- To limit processing: `.\scripts\score-agent-conversations.ps1 -Limit 10`
- To evaluate without writing results (smoke test): `.\scripts\score-agent-conversations.ps1 -Rescore -DryRun`

Scores are computed from logical events, not raw stream lines: one unique `stepId` = one tool call (terminal commands included), duplicates are detected by name + real arguments, turns = text-bearing user prompts + assistant output blocks, and sessions without tool/token telemetry are reported as no-data. See the measurement rules in `.ai/skills/conversation-evaluator/SKILL.md`.

Analyze the summary in `.agent-logs/evaluations/optimization-report.md` and use the `conversation-evaluator` agent with `agent-self-improvement` to apply necessary updates to underperforming agents.
