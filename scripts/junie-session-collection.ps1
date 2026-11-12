<#
.SYNOPSIS
  Collects Junie and JetBrains Air session traces and prompt history for the current project.

.DESCRIPTION
  Scans the Junie home directory (~/.junie/sessions/index.jsonl) for sessions matching
  the current project path, copying session transcripts, tool calls, and event streams
  into a local project directory (.agent-logs) for offline analysis.

.USAGE
  .\scripts\junie-session-collection.ps1
  .\scripts\junie-session-collection.ps1 -Limit 10
  .\scripts\junie-session-collection.ps1 -ProjectPath "C:\Users\someone\IdeaProjects\Microservices"
  .\scripts\junie-session-collection.ps1 -DryRun
#>
[CmdletBinding()]
param (
  [Parameter(Position = 0)]
  [string]$ProjectPath,

  [Parameter(Position = 1)]
  [string]$TargetDir,

  [string]$JunieHome,

  [int]$Limit = 0,

  [switch]$Force,

  [switch]$DryRun
)

$ErrorActionPreference = 'Stop'

# 1. Resolve Project Path
if (-not $ProjectPath) {
  if ($PSScriptRoot) {
    # Default to parent directory of the 'scripts' folder
    $ProjectPath = Split-Path -Path $PSScriptRoot -Parent
  } else {
    $ProjectPath = (Get-Location).Path
  }
}

if (-not (Test-Path $ProjectPath)) {
  Write-Error "Project path does not exist: $ProjectPath"
  return
}

$normalizedProjectPath = [System.IO.Path]::GetFullPath($ProjectPath).TrimEnd('\', '/')
Write-Host "Project directory : $normalizedProjectPath"

# 2. Resolve Junie Home
if (-not $JunieHome) {
  if ($env:JUNIE_HOME) {
    $JunieHome = $env:JUNIE_HOME
  } else {
    $JunieHome = Join-Path $HOME ".junie"
  }
}

if (-not (Test-Path $JunieHome)) {
  Write-Error "Junie home directory does not exist: $JunieHome"
  return
}

$normalizedJunieHome = [System.IO.Path]::GetFullPath($JunieHome).TrimEnd('\', '/')
Write-Host "Junie home        : $normalizedJunieHome"

# 3. Locate Sessions and Index
$sessionsDir = Join-Path $normalizedJunieHome "sessions"
$indexFile = Join-Path $sessionsDir "index.jsonl"

if (-not (Test-Path $indexFile)) {
  Write-Error "Session index file not found at: $indexFile. Please ensure Junie has recorded sessions."
  return
}

# 4. Resolve Target Directory
if (-not $TargetDir) {
  $TargetDir = Join-Path $normalizedProjectPath ".agent-logs"
}

Write-Host "Target directory  : $TargetDir"
if ($DryRun) {
  Write-Host "[DRY RUN MODE] No files will be copied." -ForegroundColor Yellow
}

# 5. Filter Matching Sessions
$allSessions = [System.Collections.Generic.List[PSCustomObject]]::new()
$distinctProjectDirs = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)

Get-Content -Path $indexFile | ForEach-Object {
  $line = $_.Trim()
  if (-not [string]::IsNullOrWhiteSpace($line)) {
    try {
      $sessionObj = $line | ConvertFrom-Json
      if ($sessionObj.projectDir) {
        $cleanDir = ($sessionObj.projectDir -replace '\\', '/').TrimEnd('/')
        $cleanProject = ($normalizedProjectPath -replace '\\', '/').TrimEnd('/')
        [void]$distinctProjectDirs.Add($cleanDir)
        if ($cleanDir -ieq $cleanProject -or ($cleanDir.EndsWith("/" + (Split-Path $normalizedProjectPath -Leaf), [System.StringComparison]::OrdinalIgnoreCase) -and (Split-Path $cleanDir -Leaf) -ieq (Split-Path $normalizedProjectPath -Leaf))) {
          $allSessions.Add($sessionObj)
        }
      }
    } catch {
      Write-Warning "Failed to parse index entry: $line"
    }
  }
}

Write-Host "Total matching sessions found: $($allSessions.Count)"

if ($allSessions.Count -eq 0) {
  Write-Warning "No sessions matched project path '$normalizedProjectPath'."
  Write-Host "Known project directories in $indexFile :" -ForegroundColor Cyan
  foreach ($dir in $distinctProjectDirs) {
    Write-Host "  - $dir"
  }
  return
}

if ($Limit -gt 0 -and $allSessions.Count -gt $Limit) {
  Write-Host "Limiting collection to the most recent $Limit sessions."
  # Sort descending by updatedAt or createdAt if available
  $sessionsToCopy = @($allSessions | Sort-Object { if ($_.updatedAt) { $_.updatedAt } else { $_.createdAt } } -Descending | Select-Object -First $Limit)
} else {
  $sessionsToCopy = @($allSessions)
}

if (-not $DryRun) {
  if (-not (Test-Path $TargetDir)) {
    New-Item -ItemType Directory -Path $TargetDir | Out-Null
  }

  # Export project-filtered index.jsonl
  $targetIndex = Join-Path $TargetDir "index.jsonl"
  $sessionsToCopy | ForEach-Object { $_ | ConvertTo-Json -Compress } | Set-Content -Path $targetIndex -Encoding UTF8

  # Export project-filtered prompt history if history.jsonl exists
  $historyFile = Join-Path $normalizedJunieHome "history.jsonl"
  if (Test-Path $historyFile) {
    $targetHistory = Join-Path $TargetDir "history.jsonl"
    $matchingHistory = [System.Collections.Generic.List[string]]::new()
    Get-Content -Path $historyFile | ForEach-Object {
      $hLine = $_.Trim()
      if (-not [string]::IsNullOrWhiteSpace($hLine)) {
        try {
          $hObj = $hLine | ConvertFrom-Json
          if ($hObj.projectPath) {
            $hProjNormalized = [System.IO.Path]::GetFullPath($hObj.projectPath).TrimEnd('\', '/')
            if ($hProjNormalized -ieq $normalizedProjectPath) {
              $matchingHistory.Add($hLine)
            }
          }
        } catch {}
      }
    }
    if ($matchingHistory.Count -gt 0) {
      Set-Content -Path $targetHistory -Value $matchingHistory -Encoding UTF8
      Write-Host "Exported $($matchingHistory.Count) prompt history records to $targetHistory"
    }
  }
}

# 6. Copy Session Folders
$copiedCount = 0
$skippedCount = 0
$total = @($sessionsToCopy).Count
$index = 0

foreach ($session in $sessionsToCopy) {
  $index++
  $sessionId = $session.sessionId
  $sourceDir = Join-Path $sessionsDir $sessionId
  $destDir = Join-Path $TargetDir $sessionId
  $title = if ($session.taskName) { $session.taskName } else { "Untitled Session" }

  if (-not (Test-Path $sourceDir)) {
    Write-Warning "[$index/$total] Source session folder missing: $sourceDir"
    continue
  }

  if ((Test-Path $destDir) -and (-not $Force)) {
    $skippedCount++
    continue
  }

  if ($DryRun) {
    Write-Host "[$index/$total] [DryRun] Would copy $sessionId ($title)"
    $copiedCount++
    continue
  }

  Write-Host "[$index/$total] Copying $sessionId ($title)..."
  try {
    Copy-Item -Path $sourceDir -Destination $destDir -Recurse -Force
    $copiedCount++
  } catch {
    Write-Warning "Failed to copy session ${sessionId}: $_"
  }
}

Write-Host "Collection complete. Copied: $copiedCount, Skipped (already existed): $skippedCount." -ForegroundColor Green
Write-Host "Sessions stored in: $TargetDir"
