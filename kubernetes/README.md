# Kubernetes Configuration for Microservices

This directory contains Kubernetes manifest files for deploying the microservices application.

For information on how to update the Kubernetes deployment, please refer to the [Kubernetes Updates Guide](kubernetes-updates.md).

## Overview

The application has been converted from Docker Compose to Kubernetes. The following resources have been created:

- **Namespace**: A dedicated namespace `microservices` for all resources
- **ConfigMap**: For non-sensitive configuration values
- **Secret**: For sensitive configuration values (passwords, API keys, etc.)
- **PersistentVolumes and PersistentVolumeClaims**: For database storage
- **Deployments and Services**: For each microservice
- **Ingress**: For external access to the gateway service

## Prerequisites

- Kubernetes cluster (local or cloud-based)
- kubectl CLI tool
- Nginx Ingress Controller installed in the cluster
- TLS certificate for the domain (stored as a Kubernetes Secret)

## Directory Structure

- `namespace.yaml`: Defines the microservices namespace
- `configmap.yaml`: Contains non-sensitive configuration
- `secrets.yaml`: Contains sensitive configuration (passwords, API keys)
- `storage.yaml`: Defines PersistentVolumes and PersistentVolumeClaims for databases
- `registry-service.yaml`: Service registry (Eureka)
- `zipkin.yaml`: Distributed tracing (Zipkin, Zipkin Storage, Zipkin Dependencies)
- `monitoring.yaml`: Monitoring tools (Prometheus, Grafana)
- `authentication-db.yaml`: Authentication database (MariaDB)
- `usermanagement-db.yaml`: User Management database (MariaDB)
- `chess-db.yaml`: Chess database (MariaDB)
- `fitness-db.yaml`: Fitness database (MariaDB)
- `music-db.yaml`: Music database (MariaDB)
- `admin-service.yaml`: Admin dashboard
- `authentication-service.yaml`: Authentication service
- `usermanagement-service.yaml`: User management service
- `gateway-service.yaml`: API gateway with Ingress configuration
- `website-service.yaml`: Website service
- `chess-service.yaml`: Chess service
- `fitness-service.yaml`: Fitness service
- `music-service.yaml`: Music service

## Deployment Instructions

1. Create the namespace:
   ```
   kubectl apply -f namespace.yaml
   ```

2. Create ConfigMap and Secret:
   ```
   kubectl apply -f configmap.yaml
   kubectl apply -f secrets.yaml
   ```

   **Note**: Before applying `secrets.yaml`, update the placeholder values with actual secrets.

3. Create storage resources:
   ```
   kubectl apply -f storage.yaml
   ```

4. Deploy infrastructure services:
   ```
   kubectl apply -f registry-service.yaml
   kubectl apply -f zipkin.yaml
   kubectl apply -f monitoring.yaml
   ```

5. Deploy database services:
   ```
   kubectl apply -f authentication-db.yaml
   kubectl apply -f usermanagement-db.yaml
   kubectl apply -f chess-db.yaml
   kubectl apply -f fitness-db.yaml
   kubectl apply -f music-db.yaml
   ```

6. Deploy application services:
   ```
   kubectl apply -f admin-service.yaml
   kubectl apply -f authentication-service.yaml
   kubectl apply -f usermanagement-service.yaml
   kubectl apply -f website-service.yaml
   kubectl apply -f chess-service.yaml
   kubectl apply -f fitness-service.yaml
   kubectl apply -f music-service.yaml
   ```

7. Deploy gateway service with Ingress:
   ```
   kubectl apply -f gateway-service.yaml
   ```

## TLS Certificate

Before deploying the gateway service, ensure you have created a TLS Secret named `michibaum-tls` in the `microservices` namespace:

```
kubectl create secret tls michibaum-tls --cert=path/to/tls.crt --key=path/to/tls.key -n microservices
```

## Monitoring

- Prometheus is available at: `http://prometheus-service.microservices.svc.cluster.local:9090`
- Grafana is available at: `http://grafana-service.microservices.svc.cluster.local:3000`

## Differences from Docker Compose

The Kubernetes configuration differs from the Docker Compose setup in the following ways:

1. **Resource Management**: Kubernetes allows for more granular control over CPU and memory resources.
2. **Scaling**: Services can be scaled independently by adjusting the number of replicas.
3. **Service Discovery**: Services use Kubernetes DNS for service discovery instead of Docker's network aliases.
4. **Configuration**: Environment variables are stored in ConfigMaps and Secrets instead of .env files.
5. **Networking**: Services communicate through Kubernetes Services instead of Docker networks.
6. **Ingress**: External access is managed through Kubernetes Ingress instead of port mapping.

## Notes

- The configuration uses hostPath volumes for simplicity. In a production environment, you should use a more robust storage solution like a cloud provider's persistent disk.
- The Ingress configuration assumes an Nginx Ingress Controller. Adjust as needed for other ingress controllers.
- Secrets contain placeholder values. Replace them with actual values before deployment.
