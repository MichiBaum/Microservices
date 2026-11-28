<#
.SYNOPSIS
  Evaluates and scores agent conversations in .agent-logs based on net utility and efficiency.

.DESCRIPTION
  Scans conversation logs in .agent-logs, parses event streams and state transcripts,
  computes quantitative and qualitative utility metrics (goal resolution, tool economy,
  error resilience, token overhead, and standards compliance), and records structured
  evaluations in .agent-logs/evaluations/.
  
  Only scores conversations that have not been scored yet, unless -Rescore is specified.
  Produces aggregated performance summaries and actionable improvement signals for agents.

.PARAMETER ProjectPath
  Path to the repository root. Defaults to the parent directory of this script.

.PARAMETER LogsDir
  Directory containing session logs. Defaults to '<ProjectPath>/.agent-logs'.

.PARAMETER OutputDir
  Directory where evaluation records and summaries are saved. Defaults to '<LogsDir>/evaluations'.

.PARAMETER SessionId
  Optional specific session ID to evaluate.

.PARAMETER Rescore
  When set, forces re-scoring of conversations that have already been evaluated.

.PARAMETER Limit
  Maximum number of unscored conversations to evaluate in this run (0 = all).

.PARAMETER DryRun
  Simulates evaluation without writing evaluation files or updating the ledger.

.PARAMETER GenerateReport
  Generates an aggregated optimization-report.md summarizing agent performance and recommendations.

.EXAMPLE
  .\scripts\score-agent-conversations.ps1
  .\scripts\score-agent-conversations.ps1 -Limit 5
  .\scripts\score-agent-conversations.ps1 -Rescore
  .\scripts\score-agent-conversations.ps1 -SessionId "session-261111-101936-10qo" -Rescore
#>
[CmdletBinding()]
param (
  [Parameter(Position = 0)]
  [string]$ProjectPath,

  [Parameter(Position = 1)]
  [string]$LogsDir,

  [string]$OutputDir,

  [string]$SessionId,

  [switch]$Rescore,

  [int]$Limit = 0,

  [switch]$DryRun,

  [bool]$GenerateReport = $true
)

$ErrorActionPreference = 'Stop'

# 1. Resolve Paths
if (-not $ProjectPath) {
  if ($PSScriptRoot) {
    $ProjectPath = Split-Path -Path $PSScriptRoot -Parent
  } else {
    $ProjectPath = (Get-Location).Path
  }
}
$normalizedProjectPath = [System.IO.Path]::GetFullPath($ProjectPath).TrimEnd('\', '/')

if (-not $LogsDir) {
  $LogsDir = Join-Path $normalizedProjectPath ".agent-logs"
}
$normalizedLogsDir = [System.IO.Path]::GetFullPath($LogsDir).TrimEnd('\', '/')

if (-not (Test-Path $normalizedLogsDir)) {
  Write-Error "Logs directory not found at: $normalizedLogsDir"
  return
}

if (-not $OutputDir) {
  $OutputDir = Join-Path $normalizedLogsDir "evaluations"
}
$normalizedOutputDir = [System.IO.Path]::GetFullPath($OutputDir).TrimEnd('\', '/')

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "       Agent Conversation Utility & Scoring Engine        " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "Project Directory  : $normalizedProjectPath"
Write-Host "Logs Directory     : $normalizedLogsDir"
Write-Host "Evaluations Output : $normalizedOutputDir"
Write-Host "Rescore Mode       : $(if ($Rescore) { 'ENABLED (rescoring all requested)' } else { 'DISABLED (unscored only)' })"
if ($DryRun) {
  Write-Host "[DRY RUN MODE] No evaluation files will be modified." -ForegroundColor Yellow
}

# 2. Load Evaluation Ledger
$ledgerFile = Join-Path $normalizedOutputDir "ledger.json"
$ledger = @{
  version = "1.0.0"
  lastUpdated = [DateTime]::UtcNow.ToString("o")
  totalScored = 0
  averageScore = 0.0
  scores = @{}
}

if (Test-Path $ledgerFile) {
  try {
    $rawContent = Get-Content -Path $ledgerFile -Raw -Encoding UTF8
    if (-not [string]::IsNullOrWhiteSpace($rawContent)) {
      $parsedLedger = $rawContent | ConvertFrom-Json
      if ($parsedLedger.scores) {
        $ledger.version = $parsedLedger.version
        $ledger.lastUpdated = $parsedLedger.lastUpdated
        $ledger.totalScored = $parsedLedger.totalScored
        $ledger.averageScore = $parsedLedger.averageScore
        # Convert PSCustomObject properties to hashtable
        foreach ($prop in $parsedLedger.scores.PSObject.Properties) {
          $ledger.scores[$prop.Name] = $prop.Value
        }
      }
    }
    Write-Host "Loaded existing evaluation ledger with $($ledger.scores.Count) previously scored sessions." -ForegroundColor DarkCyan
  } catch {
    Write-Warning "Could not parse existing ledger file ($ledgerFile). A new ledger will be initialized."
  }
}

# 3. Discover Conversation Directories
$sessionFolders = Get-ChildItem -Path $normalizedLogsDir -Directory | Where-Object {
  $_.Name -ne "evaluations" -and ($_.Name -like "session-*" -or $_.Name -like "aia-chat-*" -or (Test-Path (Join-Path $_.FullName "events.jsonl")))
}

if ($SessionId) {
  $sessionFolders = $sessionFolders | Where-Object { $_.Name -ieq $SessionId }
  if ($sessionFolders.Count -eq 0) {
    Write-Error "Specified session '$SessionId' was not found in $normalizedLogsDir."
    return
  }
}

Write-Host "Discovered $($sessionFolders.Count) total conversation directories in logs."

# 4. Filter for Unscored Sessions (unless -Rescore)
$pendingSessions = [System.Collections.Generic.List[System.IO.DirectoryInfo]]::new()
$skippedCount = 0

foreach ($folder in $sessionFolders) {
  $sid = $folder.Name
  $evalFilePath = Join-Path $normalizedOutputDir "$sid.json"
  $isAlreadyScored = $ledger.scores.ContainsKey($sid) -and (Test-Path $evalFilePath)

  if ($isAlreadyScored -and (-not $Rescore)) {
    $skippedCount++
  } else {
    $pendingSessions.Add($folder)
  }
}

Write-Host "Sessions to evaluate : $($pendingSessions.Count)"
Write-Host "Already scored       : $skippedCount (skipped; pass -Rescore to re-evaluate)"

if ($pendingSessions.Count -eq 0) {
  Write-Host "All sessions have already been scored. No new conversations to process." -ForegroundColor Green
  if (-not $GenerateReport -or $ledger.totalScored -eq 0) {
    return
  }
}

if ($Limit -gt 0 -and $pendingSessions.Count -gt $Limit) {
  Write-Host "Applying limit: evaluating first $Limit sessions." -ForegroundColor Yellow
  $limitedArray = @($pendingSessions | Select-Object -First $Limit)
  $pendingSessions = [System.Collections.Generic.List[System.IO.DirectoryInfo]]::new([System.IO.DirectoryInfo[]]$limitedArray)
}

if (-not $DryRun) {
  if (-not (Test-Path $normalizedOutputDir)) {
    New-Item -ItemType Directory -Path $normalizedOutputDir -Force | Out-Null
  }
}

# Helper Function: Parse and Evaluate a Single Conversation
function Evaluate-Conversation {
  param (
    [System.IO.DirectoryInfo]$Folder
  )

  $sid = $Folder.Name
  $eventsFile = Join-Path $Folder.FullName "events.jsonl"
  $stateFile = Join-Path $Folder.FullName "state.json"
  $summaryFile = Join-Path $Folder.FullName "summary.json"

  $eval = [ordered]@{
    sessionId = $sid
    evaluatedAt = [DateTime]::UtcNow.ToString("o")
    sessionType = if ($sid -like "aia-chat-*") { "JetBrains Air" } else { "Junie CLI" }
    taskName = ""
    userPrompts = [System.Collections.Generic.List[string]]::new()
    metrics = [ordered]@{
      totalTurns = 0
      userPromptCount = 0
      assistantResponseCount = 0
      totalToolCalls = 0
      successfulToolCalls = 0
      failedToolCalls = 0
      duplicateToolCalls = 0
      totalInputTokens = 0
      totalOutputTokens = 0
      estimatedCost = 0.0
      userCorrectionSignals = 0
      verificationActions = 0
      standardsComplianceSignals = 0
      taskFailureEvents = 0
      canceledCommands = 0
      tokenTelemetryAvailable = $false
      hasToolTelemetry = $false
      lastTaskState = ""
      hasTerminalCompletion = $false
    }
    toolUsage = @{}
    toolCallSignatures = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    findings = [System.Collections.Generic.List[string]]::new()
    penalties = [System.Collections.Generic.List[PSCustomObject]]::new()
    bonuses = [System.Collections.Generic.List[PSCustomObject]]::new()
    scores = [ordered]@{
      taskResolutionScore = 100.0  # 35% weight
      toolEfficiencyScore = 100.0  # 25% weight
      tokenEconomyScore   = 100.0  # 15% weight
      errorResilienceScore= 100.0  # 15% weight
      standardsScore      = 100.0  # 10% weight
      compositeUtilityScore = 100.0
      grade = "A"
      utilityCategory = "High Utility"
    }
    agentRecommendations = [System.Collections.Generic.List[string]]::new()
    evaluatorImprovementSignals = [System.Collections.Generic.List[string]]::new()
  }

  # Parse Task Name from index.jsonl if available
  $indexFile = Join-Path (Split-Path $Folder.FullName -Parent) "index.jsonl"
  if (Test-Path $indexFile) {
    try {
      foreach ($idxLine in [System.IO.File]::ReadLines($indexFile)) {
        if ($idxLine -match [regex]::Escape($sid)) {
          $idxObj = $idxLine | ConvertFrom-Json
          if ($idxObj.taskName) { $eval.taskName = $idxObj.taskName; break }
        }
      }
    } catch {}
  }

  # Logical tool calls are keyed by stepId: one unique stepId = one logical call,
  # even though Junie emits several update events per step (IN_PROGRESS -> COMPLETED).
  $toolSteps = [System.Collections.Specialized.OrderedDictionary]::new()
  $assistantSteps = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::Ordinal)
  $legacyCompleted = $false
  $anonToolSeq = 0
  $assistantNoStepId = 0
  # Junie reports each task failure twice: a top-level AgentTaskFailedEvent and a
  # SessionA2uxEvent wrapper whose agentEvent.kind is "AgentFailureEvent".
  # Distinct failures per session = max(top-level, wrapped).
  $topFailures = 0
  $wrapFailures = 0

  # Build an argument-based signature from real tool arguments (details/text/command),
  # not from display text (e.g. every web fetch displays as "Fetch URL").
  function Get-ToolArgSignature {
    param($toolEvent)
    $parts = @()
    $details = $toolEvent.details
    if ($null -ne $details) {
      if ($details -is [System.Management.Automation.PSCustomObject]) {
        foreach ($prop in $details.PSObject.Properties) {
          $v = $prop.Value
          if ($null -ne $v) { $parts += ([string]$v).Trim() }
        }
      } elseif ($details -is [System.Collections.IEnumerable] -and $details -isnot [string]) {
        foreach ($item in $details) { if ($null -ne $item) { $parts += ([string]$item).Trim() } }
      } else {
        $parts += ([string]$details).Trim()
      }
    }
    if ($toolEvent.text) { $parts += ([string]$toolEvent.text).Trim() }
    if ($toolEvent.command) { $parts += ([string]$toolEvent.command).Trim() }
    return (($parts -join " | ") -replace '\s+', ' ').ToLowerInvariant().Trim()
  }

  # Parse Events stream line by line
  if (Test-Path $eventsFile) {
    $lineReader = [System.IO.File]::ReadLines($eventsFile)
    foreach ($rawLine in $lineReader) {
      if ([string]::IsNullOrWhiteSpace($rawLine)) { continue }

      try {
        $eventObj = $rawLine | ConvertFrom-Json
      } catch {
        continue
      }

      # User Prompt Event
      if ($eventObj.kind -eq "UserPromptEvent" -or $eventObj.prompt) {
        $p = if ($eventObj.prompt) { $eventObj.prompt } else { $eventObj.presentablePrompt }
        if ($p) {
          $eval.userPrompts.Add($p)
          $eval.metrics.userPromptCount++
          if (-not $eval.taskName) {
            $eval.taskName = if ($p.Length -gt 60) { $p.Substring(0, 57) + "..." } else { $p }
          }
          # Detect user dissatisfaction / correction signals (only on follow-up prompts)
          if ($eval.metrics.userPromptCount -gt 1) {
            # Filter out diagnostic log traces or stack traces pasted by the user for troubleshooting
            $isDiagnosticLog = $p -match "^\s*(\d{4}-\d{2}-\d{2}|\[\w+\]|Exception in thread|Servlet\.service\(\)|\tat\s+[a-zA-Z0-9_.]+|SQL State\s*:|Error Code\s*:)"
            if (-not $isDiagnosticLog -and $p -match "(?i)\b(doesn't work|did not work|still failing|incorrect|wrong|revert|you broke|not working|broken|that is not right|bullshit|stfu|just fix it|never executed|talking nonsense|not what i asked|no overload matches)\b") {
              $eval.metrics.userCorrectionSignals++
            }
          }
        }
      }

      # Model Token and Cost Usage
      if ($eventObj.event.agentEvent.kind -eq "LlmResponseMetadataEvent") {
        $usageList = $eventObj.event.agentEvent.modelUsage
        if ($usageList) {
          foreach ($u in $usageList) {
            if ($u.inputTokens) { $eval.metrics.totalInputTokens += [int64]$u.inputTokens }
            if ($u.outputTokens) { $eval.metrics.totalOutputTokens += [int64]$u.outputTokens }
            if ($u.cost) { $eval.metrics.estimatedCost += [double]$u.cost }
          }
          $eval.metrics.tokenTelemetryAvailable = $true
        }
      }

      # Tool Blocks & Invocations (logical calls keyed by stepId)
      # Junie emits several update events per logical call (IN_PROGRESS -> COMPLETED);
      # TerminalBlockUpdatedEvent carries shell commands with their own stepIds.
      $toolEvent = $null
      if ($eventObj.event.agentEvent.kind -eq "ToolBlockUpdatedEvent") {
        $toolEvent = $eventObj.event.agentEvent
      } elseif ($eventObj.event.agentEvent.kind -eq "TerminalBlockUpdatedEvent") {
        $toolEvent = $eventObj.event.agentEvent
      } elseif ($eventObj.kind -eq "ExecuteToolFinishedEvent") {
        $toolEvent = $eventObj
      }

      if ($toolEvent) {
        $eval.metrics.hasToolTelemetry = $true
        $toolName = if ($toolEvent.toolType) {
          $toolEvent.toolType
        } elseif ($toolEvent.name) {
          $toolEvent.name
        } elseif ($eventObj.event.agentEvent.kind -eq "TerminalBlockUpdatedEvent") {
          "terminal"
        } else {
          "UnknownTool"
        }
        $status = [string]$toolEvent.status
        if ([string]::IsNullOrWhiteSpace($status)) { $status = "COMPLETED" }

        # Argument-based signature from real arguments (details/text/command), not display text.
        $argSig = Get-ToolArgSignature -toolEvent $toolEvent
        $sigKey = "$toolName::$argSig"

        # One logical call per unique stepId; later streaming updates overwrite earlier ones.
        if ([string]::IsNullOrWhiteSpace([string]$toolEvent.stepId)) {
          $anonToolSeq++
          $stepKey = "anon:$anonToolSeq"
        } else {
          $prefix = if ($eventObj.event.agentEvent.kind -eq "TerminalBlockUpdatedEvent") { "term:" } else { "tool:" }
          $stepKey = $prefix + [string]$toolEvent.stepId
        }
        if ($toolSteps.Contains($stepKey)) {
          $existing = $toolSteps[$stepKey]
          $toolSteps[$stepKey] = [ordered]@{
            name   = if ($toolName -ne "UnknownTool") { $toolName } else { $existing.name }
            status = if (-not [string]::IsNullOrWhiteSpace($status)) { $status } else { $existing.status }
            sig    = if (-not [string]::IsNullOrWhiteSpace($argSig)) { $argSig } else { $existing.sig }
            output = if (-not [string]::IsNullOrWhiteSpace([string]$toolEvent.output)) { [string]$toolEvent.output } else { $existing.output }
          }
        } else {
          $toolSteps[$stepKey] = [ordered]@{
            name   = $toolName
            status = $status
            sig    = $argSig
            output = [string]$toolEvent.output
          }
        }
      }

      # Assistant output blocks, deduped by stepId (streaming updates share a stepId).
      # Junie emits ResultBlockUpdatedEvent; JetBrains Air emits MarkdownBlockUpdatedEvent.
      $assistantKind = $eventObj.event.agentEvent.kind
      if ($assistantKind -eq "ResultBlockUpdatedEvent" -or $assistantKind -eq "MarkdownBlockUpdatedEvent") {
        $aid = [string]$eventObj.event.agentEvent.stepId
        if (-not [string]::IsNullOrWhiteSpace($aid)) {
          [void]$assistantSteps.Add($aid)
        } else {
          $assistantNoStepId++
        }
      }

      # Task failure signals: Junie emits each failure twice (top-level AgentTaskFailedEvent
      # plus a SessionA2uxEvent wrapper with agentEvent.kind = "AgentFailureEvent"),
      # so count both forms and dedupe per session below.
      if ($eventObj.kind -eq "AgentTaskFailedEvent") {
        $topFailures++
      } elseif ($eventObj.event.agentEvent.kind -eq "AgentFailureEvent") {
        $wrapFailures++
      }

      # Track the last observed task state (Junie TaskState events, wrapper states)
      if ($eventObj.kind -eq "TaskState" -and $eventObj.state) {
        $eval.metrics.lastTaskState = [string]$eventObj.state
      } elseif ($eventObj.event.state) {
        $eval.metrics.lastTaskState = [string]$eventObj.event.state
      }

      # Legacy completion fallback (sessions without TaskState telemetry)
      if ($eventObj.kind -eq "TaskFinishedEvent" -or $eventObj.event.agentEvent.kind -eq "ResultBlockUpdatedEvent") {
        $legacyCompleted = $true
      }
    }
  }

  # Dedupe task failures: each logical failure appears in both event forms.
  $eval.metrics.taskFailureEvents = [Math]::Max($topFailures, $wrapFailures)

  # Aggregate logical tool calls (one entry per unique stepId)
  foreach ($entry in $toolSteps.Values) {
    $eval.metrics.totalToolCalls++
    $name = [string]$entry.name
    if (-not $eval.toolUsage.ContainsKey($name)) {
      $eval.toolUsage[$name] = [ordered]@{ count = 0; failed = 0; duplicate = 0 }
    }
    $eval.toolUsage[$name].count++

    $status = [string]$entry.status
    $argSig = [string]$entry.sig
    $sigKey = "$name::$argSig"
    # Duplicate detection only when real arguments exist (e.g. "Create file" events carry no args).
    if (-not [string]::IsNullOrWhiteSpace($argSig)) {
      if ($eval.toolCallSignatures.Contains($sigKey)) {
        $eval.metrics.duplicateToolCalls++
        $eval.toolUsage[$name].duplicate++
      } else {
        [void]$eval.toolCallSignatures.Add($sigKey)
      }
    }

    # Failure: explicit FAILED/REJECTED status, or failure text in the output.
    $outputText = [string]$entry.output
    $isFailure = ($status -eq "FAILED" -or $status -eq "REJECTED") -or
                 (-not [string]::IsNullOrWhiteSpace($outputText) -and $outputText -match "(?i)(exit code [1-9]|command failed|syntax error|fatal error|BUILD FAILURE)")
    if ($isFailure) {
      $eval.metrics.failedToolCalls++
      $eval.toolUsage[$name].failed++
    } else {
      $eval.metrics.successfulToolCalls++
    }
    if ($status -eq "CANCELED") {
      $eval.metrics.canceledCommands++
    }

    # Verification / standards signals from actual command/argument text
    if ($sigKey -match "(?i)(mvnw (test|clean test)|npm test|verify|check|archunit|qodana)") {
      $eval.metrics.verificationActions++
    }
    if ($sigKey -match "(?i)(archunittests|Guidelines\.md|Clean Code|Writerside)") {
      $eval.metrics.standardsComplianceSignals++
    }
  }

  # Logical turns = user prompts + assistant output blocks (not raw event lines;
  # Junie streams are dominated by status-update events).
  $eval.metrics.assistantResponseCount = $assistantSteps.Count + $assistantNoStepId
  $eval.metrics.totalTurns = $eval.metrics.userPromptCount + $eval.metrics.assistantResponseCount

  # Terminal completion: last observed task state wins; fall back to legacy signals.
  if ($eval.metrics.lastTaskState -eq "COMPLETED") {
    $eval.metrics.hasTerminalCompletion = $true
  } elseif ([string]::IsNullOrWhiteSpace($eval.metrics.lastTaskState)) {
    $eval.metrics.hasTerminalCompletion = $legacyCompleted
  }

  # Also inspect state.json for JetBrains Air or fallback
  if ((Test-Path $stateFile) -and ($eval.metrics.totalToolCalls -eq 0)) {
    try {
      $stateContent = Get-Content -Path $stateFile -Raw -Encoding UTF8
      if ($stateContent -match '"isProcessed":true') {
        $eval.metrics.hasTerminalCompletion = $true
      }
      if ($stateContent -match 'previousPromptDescriptions') {
        $matches = [regex]::Matches($stateContent, '"previousPromptDescriptions":\["(.*?)"\]')
        if ($matches.Count -gt 0 -and (-not $eval.taskName)) {
          $eval.taskName = $matches[0].Groups[1].Value
        }
      }
    } catch {}
  }

  # =========================================================================
  # Compute Utility Dimension Scores (0 - 100)
  # =========================================================================

  # 1. Task Resolution Score (Weight: 35%)
  $resScore = 100.0
  if (-not $eval.metrics.hasTerminalCompletion) {
    $resScore -= 30.0
    $eval.penalties.Add([PSCustomObject]@{ metric = "TaskResolution"; points = -30; reason = "Session ended without a verified terminal completion" })
  }
  if ($eval.metrics.userCorrectionSignals -gt 0) {
    $penalty = [Math]::Min(35.0, $eval.metrics.userCorrectionSignals * 10.0)
    $resScore -= $penalty
    $eval.penalties.Add([PSCustomObject]@{ metric = "UserCorrection"; points = -$penalty; reason = "$($eval.metrics.userCorrectionSignals) user correction/dissatisfaction prompts detected" })
  }
  if ($eval.metrics.taskFailureEvents -gt 0) {
    $penalty = [Math]::Min(30.0, $eval.metrics.taskFailureEvents * 10.0)
    $resScore -= $penalty
    $eval.penalties.Add([PSCustomObject]@{ metric = "TaskFailures"; points = -$penalty; reason = "$($eval.metrics.taskFailureEvents) agent-reported task failure events recorded" })
  }
  $eval.scores.taskResolutionScore = [Math]::Max(0.0, [Math]::Round($resScore, 1))

  # 2. Tool Efficiency Score (Weight: 25%) - no-data when the session has no tool telemetry
  $toolScore = 100.0
  if ($eval.metrics.hasToolTelemetry) {
    if ($eval.metrics.totalToolCalls -gt 0) {
      # Failure penalty
      $failRatio = $eval.metrics.failedToolCalls / [double]$eval.metrics.totalToolCalls
      $failPenalty = [Math]::Min(40.0, [Math]::Round($failRatio * 60.0, 1))
      if ($failPenalty -gt 0) {
        $toolScore -= $failPenalty
        $eval.penalties.Add([PSCustomObject]@{ metric = "ToolFailures"; points = -$failPenalty; reason = "$($eval.metrics.failedToolCalls) failed tool calls ($([Math]::Round($failRatio * 100, 1))% failure rate)" })
      }
      # Duplicate / Redundancy penalty
      $dupPenalty = [Math]::Min(30.0, $eval.metrics.duplicateToolCalls * 5.0)
      if ($dupPenalty -gt 0) {
        $toolScore -= $dupPenalty
        $eval.penalties.Add([PSCustomObject]@{ metric = "ToolRedundancy"; points = -$dupPenalty; reason = "$($eval.metrics.duplicateToolCalls) redundant identical tool invocations" })
      }
    }
  } else {
    $eval.findings.Add("No tool telemetry available for this session (JetBrains Air chat format); tool efficiency reported as no-data.")
  }
  $eval.scores.toolEfficiencyScore = [Math]::Max(0.0, [Math]::Round($toolScore, 1))

  # 3. Token Economy & Resource Overhead (Weight: 15%)
  $tokenScore = 100.0
  $totalTokens = $eval.metrics.totalInputTokens + $eval.metrics.totalOutputTokens
  if (-not $eval.metrics.tokenTelemetryAvailable) {
    $eval.findings.Add("No token/cost telemetry available for this session; token economy reported as no-data.")
  }
  if ($totalTokens -gt 300000) {
    $tokenScore -= 25.0
    $eval.penalties.Add([PSCustomObject]@{ metric = "TokenOverhead"; points = -25; reason = "High token consumption ($totalTokens tokens)" })
  } elseif ($totalTokens -gt 150000) {
    $tokenScore -= 12.0
    $eval.penalties.Add([PSCustomObject]@{ metric = "TokenOverhead"; points = -12; reason = "Moderate token consumption ($totalTokens tokens)" })
  }
  if ($eval.metrics.totalTurns -gt 50) {
    $tokenScore -= 15.0
    $eval.penalties.Add([PSCustomObject]@{ metric = "TurnOverhead"; points = -15; reason = "High logical turn count ($($eval.metrics.totalTurns) user+assistant turns)" })
  } elseif ($eval.metrics.userPromptCount -gt 8 -and $eval.metrics.totalToolCalls -eq 0) {
    $tokenScore -= 10.0
    $eval.penalties.Add([PSCustomObject]@{ metric = "ConversationalDrag"; points = -10; reason = "Conversational drag: excessive back-and-forth ($($eval.metrics.userPromptCount) user turns) without execution" })
  }
  $eval.scores.tokenEconomyScore = [Math]::Max(0.0, [Math]::Round($tokenScore, 1))

  # 4. Error Resilience & Recovery (Weight: 15%)
  $errScore = 100.0
  if ($eval.metrics.failedToolCalls -gt 0) {
    if ($eval.metrics.hasTerminalCompletion -and $eval.metrics.userCorrectionSignals -eq 0) {
      # Recovered autonomously!
      $errScore -= [Math]::Min(20.0, $eval.metrics.failedToolCalls * 3.0)
      $eval.bonuses.Add([PSCustomObject]@{ metric = "AutonomousRecovery"; points = +5; reason = "Successfully recovered from tool failures without user intervention" })
    } else {
      $errScore -= [Math]::Min(50.0, $eval.metrics.failedToolCalls * 8.0)
    }
  }
  $eval.scores.errorResilienceScore = [Math]::Max(0.0, [Math]::Round($errScore, 1))

  # 5. Standards & Verification Compliance (Weight: 10%)
  $stdScore = 80.0 # baseline
  if ($eval.metrics.verificationActions -gt 0) {
    $stdScore += 15.0
    $eval.bonuses.Add([PSCustomObject]@{ metric = "ActiveVerification"; points = +15; reason = "Executed automated verification checks (test/verify)" })
  }
  if ($eval.metrics.standardsComplianceSignals -gt 0) {
    $stdScore += 5.0
    $eval.bonuses.Add([PSCustomObject]@{ metric = "StandardsCompliance"; points = +5; reason = "Referenced project architectural standards or guidelines" })
  }
  $eval.scores.standardsScore = [Math]::Min(100.0, [Math]::Max(0.0, [Math]::Round($stdScore, 1)))

  # Composite Net Utility Score
  # Dynamically renormalize weights when tool or token telemetry is absent to uphold the "no-data, not free points" policy
  $weights = [ordered]@{
    taskResolution  = 0.35
    toolEfficiency  = if ($eval.metrics.hasToolTelemetry) { 0.25 } else { 0.0 }
    tokenEconomy    = if ($eval.metrics.tokenTelemetryAvailable) { 0.15 } else { 0.0 }
    errorResilience = 0.15
    standards       = 0.10
  }
  $weightSum = ($weights.Values | Measure-Object -Sum).Sum
  $weightedSum = (
    ($eval.scores.taskResolutionScore  * $weights.taskResolution) +
    ($eval.scores.toolEfficiencyScore  * $weights.toolEfficiency) +
    ($eval.scores.tokenEconomyScore    * $weights.tokenEconomy) +
    ($eval.scores.errorResilienceScore * $weights.errorResilience) +
    ($eval.scores.standardsScore       * $weights.standards)
  )
  $composite = if ($weightSum -gt 0) { $weightedSum / $weightSum } else { 0.0 }
  $eval.scores.compositeUtilityScore = [Math]::Round($composite, 1)

  # Assign Grade & Category
  $c = $eval.scores.compositeUtilityScore
  if ($c -ge 95) { $eval.scores.grade = "A+"; $eval.scores.utilityCategory = "Optimal Utility" }
  elseif ($c -ge 85) { $eval.scores.grade = "A";  $eval.scores.utilityCategory = "High Utility" }
  elseif ($c -ge 75) { $eval.scores.grade = "B";  $eval.scores.utilityCategory = "Moderate Utility" }
  elseif ($c -ge 65) { $eval.scores.grade = "C";  $eval.scores.utilityCategory = "Sub-optimal Utility" }
  elseif ($c -ge 50) { $eval.scores.grade = "D";  $eval.scores.utilityCategory = "Marginal Utility" }
  else { $eval.scores.grade = "F";  $eval.scores.utilityCategory = "Deficit / Wasteful" }

  # =========================================================================
  # Generate Agent & Self Improvement Signals
  # =========================================================================
  if ($eval.metrics.duplicateToolCalls -gt 0) {
    $eval.agentRecommendations.Add("Reduce repetitive file reads and duplicate searches by caching inspected context in memory.")
  }
  if ($eval.metrics.failedToolCalls -gt 2) {
    $eval.agentRecommendations.Add("High tool failure rate detected ($($eval.metrics.failedToolCalls) failures). Enhance tool call argument pre-validation (e.g. check path separators, command syntax).")
  }
  if ($eval.metrics.verificationActions -eq 0 -and $eval.metrics.totalToolCalls -gt 5) {
    $eval.agentRecommendations.Add("No verification command executed. Domain agents must validate code changes with automated test commands prior to finishing.")
  }
  if ($eval.metrics.userCorrectionSignals -gt 0) {
    $eval.agentRecommendations.Add("User intervened to correct agent errors. Agents should proactively request clarification on ambiguity rather than guessing.")
  }

  # Self-improvement signals for the evaluator
  if ($eval.metrics.totalToolCalls -eq 0 -and $eval.metrics.totalTurns -gt 0) {
    $eval.evaluatorImprovementSignals.Add("Evaluator should parse chat-only turns to differentiate pure conversational advice from software engineering sessions.")
  }
  if ($eval.scores.compositeUtilityScore -lt 60.0) {
    $eval.evaluatorImprovementSignals.Add("Flag session for qualitative human/expert review due to low composite utility score.")
  }

  return $eval
}

# 5. Process Sessions and Update Ledger
$processedCount = 0
$scoresRecorded = [System.Collections.Generic.List[PSCustomObject]]::new()

Write-Host "Scoring conversations in progress..." -ForegroundColor Cyan

foreach ($folder in $pendingSessions) {
  $sid = $folder.Name
  Write-Host "Evaluating [$($processedCount + 1)/$($pendingSessions.Count)] $sid ... " -NoNewline

  try {
    $evaluationResult = Evaluate-Conversation -Folder $folder
    $score = $evaluationResult.scores.compositeUtilityScore
    $grade = $evaluationResult.scores.grade

    Write-Host "$score/100 ($grade)" -ForegroundColor $(
      if ($score -ge 85) { "Green" }
      elseif ($score -ge 70) { "Yellow" }
      else { "Red" }
    )

    if (-not $DryRun) {
      $destFile = Join-Path $normalizedOutputDir "$sid.json"
      $evaluationResult | ConvertTo-Json -Depth 6 | Set-Content -Path $destFile -Encoding UTF8

      # Update Ledger Entry
      $ledgerEntry = [ordered]@{
        sessionId = $sid
        taskName = $evaluationResult.taskName
        sessionType = $evaluationResult.sessionType
        score = $score
        grade = $grade
        utilityCategory = $evaluationResult.scores.utilityCategory
        totalToolCalls = $evaluationResult.metrics.totalToolCalls
        failedToolCalls = $evaluationResult.metrics.failedToolCalls
        duplicateToolCalls = $evaluationResult.metrics.duplicateToolCalls
        totalTurns = $evaluationResult.metrics.totalTurns
        userCorrectionSignals = $evaluationResult.metrics.userCorrectionSignals
        taskFailureEvents = $evaluationResult.metrics.taskFailureEvents
        canceledCommands = $evaluationResult.metrics.canceledCommands
        hasToolTelemetry = $evaluationResult.metrics.hasToolTelemetry
        tokenTelemetryAvailable = $evaluationResult.metrics.tokenTelemetryAvailable
        scoredAt = $evaluationResult.evaluatedAt
        evaluationFile = "$sid.json"
      }
      $ledger.scores[$sid] = $ledgerEntry
      $scoresRecorded.Add([PSCustomObject]$ledgerEntry)
    }

    $processedCount++
  } catch {
    Write-Warning "Failed to evaluate conversation $sid : $_"
  }
}

# 6. Save Ledger and Compute Aggregates
if (-not $DryRun) {
  $allScoredList = @($ledger.scores.Values | ForEach-Object { if ($_ -is [System.Collections.IDictionary]) { [PSCustomObject]$_ } else { $_ } })
  $ledger.totalScored = $allScoredList.Count
  if ($allScoredList.Count -gt 0) {
    $measureResult = ($allScoredList | Measure-Object -Property score -Average)
    $avg = if ($measureResult.Average -ne $null) { $measureResult.Average } else { 0.0 }
    $ledger.averageScore = [Math]::Round([double]$avg, 2)
  }
  $ledger.lastUpdated = [DateTime]::UtcNow.ToString("o")

  # Write back ledger
  $ledger | ConvertTo-Json -Depth 6 | Set-Content -Path $ledgerFile -Encoding UTF8
  Write-Host "`nUpdated evaluation ledger at: $ledgerFile (Total Scored: $($ledger.totalScored), Average Score: $($ledger.averageScore))" -ForegroundColor Green
}

# 7. Generate Aggregated Optimization Report
if ($GenerateReport -and (-not $DryRun) -and $ledger.totalScored -gt 0) {
  $summaryJsonFile = Join-Path $normalizedOutputDir "summary.json"
  $reportMdFile = Join-Path $normalizedOutputDir "optimization-report.md"

  $allScored = @($ledger.scores.Values | ForEach-Object { if ($_ -is [System.Collections.IDictionary]) { [PSCustomObject]$_ } else { $_ } })
  $gradeCounts = @{
    "A+" = @($allScored | Where-Object { $_.grade -eq "A+" }).Count
    "A"  = @($allScored | Where-Object { $_.grade -eq "A" }).Count
    "B"  = @($allScored | Where-Object { $_.grade -eq "B" }).Count
    "C"  = @($allScored | Where-Object { $_.grade -eq "C" }).Count
    "D"  = @($allScored | Where-Object { $_.grade -eq "D" }).Count
    "F"  = @($allScored | Where-Object { $_.grade -eq "F" }).Count
  }

  $totalTools = ($allScored | Measure-Object -Property totalToolCalls -Sum).Sum
  $totalFailedTools = ($allScored | Measure-Object -Property failedToolCalls -Sum).Sum
  $totalDuplicates = ($allScored | Measure-Object -Property duplicateToolCalls -Sum).Sum
  $totalCorrections = ($allScored | Measure-Object -Property userCorrectionSignals -Sum).Sum
  # New measurement fields (safe on mixed old/new ledger entries)
  $totalTaskFailures = ($allScored | ForEach-Object { [int]$_.taskFailureEvents } | Measure-Object -Sum).Sum
  $totalCanceled = ($allScored | ForEach-Object { [int]$_.canceledCommands } | Measure-Object -Sum).Sum
  $noToolTelemetrySessions = @($allScored | Where-Object { [bool]$_.hasToolTelemetry -eq $false }).Count
  $noTokenTelemetrySessions = @($allScored | Where-Object { [bool]$_.tokenTelemetryAvailable -eq $false }).Count

  $summaryObj = [ordered]@{
    generatedAt = [DateTime]::UtcNow.ToString("o")
    totalConversationsEvaluated = $ledger.totalScored
    averageUtilityScore = $ledger.averageScore
    gradeDistribution = $gradeCounts
    toolTelemetry = [ordered]@{
      totalToolCalls = $totalTools
      totalFailedToolCalls = $totalFailedTools
      totalDuplicateCalls = $totalDuplicates
      overallToolFailureRate = if ($totalTools -gt 0) { [Math]::Round(($totalFailedTools / [double]$totalTools) * 100, 2) } else { 0.0 }
      overallToolRedundancyRate = if ($totalTools -gt 0) { [Math]::Round(($totalDuplicates / [double]$totalTools) * 100, 2) } else { 0.0 }
      totalTaskFailureEvents = $totalTaskFailures
      totalCanceledCommands = $totalCanceled
      noToolTelemetrySessions = $noToolTelemetrySessions
      noTokenTelemetrySessions = $noTokenTelemetrySessions
    }
    topUnderperformingSessions = @(
      $allScored | Sort-Object -Property score | Select-Object -First 5 | ForEach-Object {
        [ordered]@{
          sessionId = $_.sessionId
          taskName = $_.taskName
          score = $_.score
          grade = $_.grade
          failedTools = $_.failedToolCalls
        }
      }
    )
    systemicRecommendations = @(
      "Enforce mandatory pre-flight syntax checks on terminal and search arguments to eliminate the $totalFailedTools failed tool calls.",
      "Incorporate in-memory context caching to eliminate duplicate invocations ($totalDuplicates duplicate tool calls identified).",
      "Ensure domain agents perform automated verification passes before marking tasks completed to minimize user correction rounds ($totalCorrections recorded)."
    )
  }

  $summaryObj | ConvertTo-Json -Depth 5 | Set-Content -Path $summaryJsonFile -Encoding UTF8

  # Generate optimization-report.md
  $reportContent = @"
# Agent Performance & Conversation Utility Optimization Report

- **Generated**: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
- **Total Sessions Evaluated**: $($ledger.totalScored)
- **Average Utility Score**: $($ledger.averageScore) / 100
- **Total Logical Tool Calls Analyzed**: $totalTools (deduplicated by stepId; Failed: $totalFailedTools, Duplicate: $totalDuplicates)

---

## 📊 Score & Grade Distribution

| Grade | Count | Percentage |
|:-----:|:-----:|:----------:|
| **A+** (95-100) | $($gradeCounts['A+']) | $([Math]::Round(($gradeCounts['A+'] / [double]$ledger.totalScored) * 100, 1))% |
| **A** (85-94)   | $($gradeCounts['A']) | $([Math]::Round(($gradeCounts['A'] / [double]$ledger.totalScored) * 100, 1))% |
| **B** (75-84)   | $($gradeCounts['B']) | $([Math]::Round(($gradeCounts['B'] / [double]$ledger.totalScored) * 100, 1))% |
| **C** (65-74)   | $($gradeCounts['C']) | $([Math]::Round(($gradeCounts['C'] / [double]$ledger.totalScored) * 100, 1))% |
| **D** (50-64)   | $($gradeCounts['D']) | $([Math]::Round(($gradeCounts['D'] / [double]$ledger.totalScored) * 100, 1))% |
| **F** (< 50)    | $($gradeCounts['F']) | $([Math]::Round(($gradeCounts['F'] / [double]$ledger.totalScored) * 100, 1))% |

---

## 🛠️ Tool Usage & Waste Telemetry

- **Aggregate Logical Tool Invocations** (one per unique stepId): $totalTools
- **Failed Invocations**: $totalFailedTools ($($summaryObj.toolTelemetry.overallToolFailureRate)%)
- **Redundant / Duplicate Invocations** (argument-based signatures): $totalDuplicates ($($summaryObj.toolTelemetry.overallToolRedundancyRate)%)
- **Agent Task Failure Events**: $totalTaskFailures
- **Canceled Commands**: $totalCanceled
- **User Correction Interventions**: $totalCorrections
- **Sessions without tool telemetry (no-data)**: $noToolTelemetrySessions
- **Sessions without token telemetry (no-data)**: $noTokenTelemetrySessions

---

## 🎯 Continuous Improvement Directives

### 1. Evaluator Self-Improvement & Calibration
- **Refine Semantic Classification**: Differentiate purely informational chats from execution tasks to prevent unjustified penalties on sessions where no tools are warranted.
- **Tune Redundancy Sensitivity**: Allow deliberate retry steps when distinct error conditions require testing alternative tool arguments.
- **Dynamic Weight Calibration**: Adjust rubric weights based on project maturity (e.g. increase verification weight during active release cycles).

### 2. Upstream Improvements for Domain Agents
- **Spring Microservices Architect (``spring-microservices-architect.md``)**:
  - Always verify service compile status with ``./mvnw compile -pl <service>`` before declaring completion.
  - Use Spring Data JPA interface/record projections for query views instead of mutating domain entities.
  - Enforce precise CORS allowed-origin patterns without shell brace expansions.
- **Angular UI/UX Specialist (``angular-ui-ux-specialist.md``)**:
  - Strictly ground reactive code in verified Angular Signals/Resource APIs (``rxResource({ loader: () => ... })``); never invent unsupported properties like ``request`` (``TS2769``).
  - Mandate pre-completion frontend verification (``npm test`` or ``npm run build`` in ``website/``).
- **Cloud & Infrastructure Engineer (``cloud-infrastructure-engineer.md``)**:
  - Provide complete, end-to-end declarative manifests (MetalLB pools, L2 advertisements, Traefik Ingress routing) upfront to prevent multi-turn conversational drag.
  - Implement clean Micrometer Tracing observation predicates/ignore-paths for Actuator and Eureka without removing security context filters.
- **Testing & Code Review Expert (``testing-code-review-expert.md``)**:
  - Enforce automated test execution on newly modified code rather than manual assertions.
  - Test negative and role-based security boundaries for ``/actuator/**`` endpoints (``401``, ``403``, ``200``).
- **Quality & Documentation Guardian (``quality-documentation-guardian.md``)**:
  - Ensure documentation updates accompany API changes to reduce user clarification cycles.
"@

  Set-Content -Path $reportMdFile -Value $reportContent -Encoding UTF8
  Write-Host "Generated optimization report at: $reportMdFile" -ForegroundColor Cyan
}

Write-Host "`nEvaluation Run Complete. Evaluated: $processedCount sessions. Skipped: $skippedCount." -ForegroundColor Green
