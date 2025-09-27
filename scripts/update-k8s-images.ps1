<#
.SYNOPSIS
  Update Kubernetes deployment images for all microservices to a specified version.

.DESCRIPTION
  Sets the container image tag for each deployment in the "microservices" namespace
  to docker.io/70131370/<service>:<version>.

.USAGE
  ./update-k8s-images.ps1 -Version 1.1.0
  # or when execution policy allows passing by position
  ./update-k8s-images.ps1 1.1.0
#>
[CmdletBinding()]
param(
  [Parameter(Mandatory = $true, Position = 0, HelpMessage = "Version tag to set, e.g. 1.1.0")]
  [ValidateNotNullOrEmpty()]
  [string]$Version
)

$ErrorActionPreference = 'Stop'

$services = @(
  'authentication-service',
  'usermanagement-service',
  'registry-service',
  'chess-service',
  'fitness-service',
  'music-service',
  'gateway-service',
  'website-service',
  'admin-service'
)

foreach ($svc in $services) {
  Write-Host "Updating $svc to version $Version..."
  kubectl set image "deployment/$svc" -n microservices "$svc=docker.io/70131370/${svc}:${Version}"
}

Write-Host "All deployments updated to version $Version."