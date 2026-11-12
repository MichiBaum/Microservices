---
name: "kubernetes-expert"
description: "Expertise in Kubernetes (k0s) deployments, orchestration, and container management. Use when creating or modifying Kubernetes manifests, configuring ingress/networking, or managing container deployments."
---
# Kubernetes Expert

## When to Use
- Creating or updating Kubernetes manifests in `kubernetes/`.
- Configuring Deployments, StatefulSets, Services, Secrets, or ConfigMaps.
- Setting up or modifying ingress rules, TLS certificates, or domain routing.
- Troubleshooting pod failures, networking issues, or resource constraints.

## Cluster Architecture
- The project uses **k0s** as the Kubernetes distribution, managed via `k0sctl`.
- All Kubernetes manifests live in the `kubernetes/` directory.
- **Traefik** is used as the ingress controller for routing and TLS termination.

## Deployment Standards
- Use `Deployment` for stateless services and `StatefulSet` for stateful workloads.
- Define resource requests and limits for all containers.
- Use `ConfigMap` for non-sensitive configuration and `Secret` for sensitive data.
- Set appropriate health checks (`livenessProbe`, `readinessProbe`) for all services.
- Use rolling update strategy with appropriate `maxSurge` and `maxUnavailable`.

## Networking & Ingress
- Configure Traefik `IngressRoute` resources for external traffic routing.
- Use TLS certificates for all external-facing endpoints.
- Define `Service` resources with appropriate port mappings for each microservice.
- **End-to-End Networking Completeness**: When configuring external access via MetalLB or Traefik, provide the complete architecture upfront: `IPAddressPool`, `L2Advertisement`, service annotations (`metallb.io/loadBalancerIPs`), and ingress routing rules. Avoid incremental trial-and-error configurations.

## Container Best Practices
- Use `eclipse-temurin:26-jre-alpine` as the base image for JVM services.
- Keep Docker images minimal; use multi-stage builds where appropriate.
- Do not run containers as root; use a non-root user in Dockerfiles.
- Tag images with specific versions; avoid `latest` tag in production manifests.

## Constraints & Verification
- All infrastructure changes must be declarative (YAML manifests in `kubernetes/`).
- Do not apply changes directly to the cluster without updating the manifests.
- Test manifest changes locally with `docker-compose.local.yml` before deploying.
- Validate manifest syntax with `kubectl apply --dry-run=client -f <file>` or `docker-compose config` before completion.
