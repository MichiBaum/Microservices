# VPN Service

The VPN Service is a microservice built with Kotlin and Spring Boot that provides a self-service way for users to create and manage their own WireGuard VPN instances within the project's Kubernetes cluster. It acts as a bridge between the application's authentication system and the Kubernetes infrastructure, allowing authorized users to provision private, isolated VPN access dynamically.

## Core Concepts

### User-Specific Deployments
Instead of relying on a single, monolithic VPN server, the VPN Service provisions a dedicated WireGuard deployment and corresponding service for each individual user. This approach ensures maximum isolation between users and simplifies individual configuration management.

### Kubernetes Integration
The service interacts directly with the Kubernetes API to orchestrate resources. It leverages pre-defined templates for Deployments and Services, dynamically injecting user-specific parameters such as target namespaces, sanitized user identifiers, and dynamically allocated ports before deploying them to the cluster.

### Dynamic Port Allocation
When a new WireGuard instance is provisioned, the service creates a Kubernetes Service of type NodePort. This allows the Kubernetes cluster to dynamically allocate an external port for the VPN connection, preventing port conflicts and streamlining the provisioning process without manual administrative overhead.

### Self-Service Architecture
Users with the appropriate permissions can create, view, and delete their own VPN deployments through the service's REST endpoints. This empowers users to manage their own VPN access and eliminates the need for manual administrator intervention. The service includes permission checks to ensure users can only affect their own VPN resources, while elevated users can manage resources globally.

### Configuration Retrieval
To establish a connection, WireGuard requires a specific peer configuration file. The VPN Service automatically reads this generated configuration file directly from the user's running WireGuard pod in Kubernetes and serves it over the API. This provides users with an immediate, ready-to-use configuration that can be imported directly into their local WireGuard clients.

## Structural Overview

### REST API Layer
The outermost layer of the service exposes standard HTTP endpoints for the complete lifecycle management of the VPN deployments. It is responsible for resolving the authenticated user context, verifying required permissions, and translating API requests into actions.

### Kubernetes Service Layer
This core business layer handles the complex interactions with the Kubernetes cluster. It is responsible for:
- Checking for the existence of deployments to prevent duplication.
- Orchestrating the creation and deletion of both Deployments and Services.
- Handling error scenarios gracefully when cluster interactions fail.
- Interrogating running pods to extract configuration files.

### Resource Templates
The deployment mechanism relies on pre-configured Kubernetes YAML templates bundled within the application resources. These templates define the necessary container images, volume mounts, and specific capabilities required for WireGuard to run securely and correctly inside the containerized cluster environment.
