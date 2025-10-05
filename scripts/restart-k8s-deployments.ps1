<#
.SYNOPSIS
  Restart all Kubernetes microservices deployments in the "microservices" namespace.

.USAGE
  ./restart-k8s-deployments.ps1
#>
[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'

$services = @(
  'authentication-service',
  'usermanagement-service',
  'registry-service',
  'admin-service',
  'chess-service',
  'fitness-service',
  'music-service',
  'gateway-service',
  'website-service'
)

foreach ($svc in $services) {
  Write-Host "Restarting $svc..."
  kubectl rollout restart "deployment/$svc" -n microservices
}

Write-Host "All restarts triggered."