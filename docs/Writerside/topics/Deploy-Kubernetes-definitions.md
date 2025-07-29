# Deploy Kubernetes definitions

This document provides information about deploying and updating the microservices application on K0s, a lightweight Kubernetes distribution.

## Overview

- **Namespace**: A dedicated namespace `microservices` for all resources
- **ConfigMap**: For non-sensitive configuration values
- **Secret**: For sensitive configuration values (passwords, API keys, etc.)
- **PersistentVolumes and PersistentVolumeClaims**: For database storage
- **Deployments and Services**: For each microservice
- **Ingress**: For external access to the gateway service

## Prerequisites

- TLS certificate for the domain (stored as a Kubernetes Secret)

## Directory Structure

- **`k0sctl.yaml`**: Definition for k0s cluster
- **`metallb`**: Configuration for MetalLB
  - `metallb-config.yaml`: Configuration for MetalLB
- **`secrets`**: Secrets for microservices
  - `secrets.yaml`: Contains sensitive configuration (passwords, API keys)
- **`microservices`**
  - `namespace.yaml`: Defines the microservices namespace
  - `configmap.yaml`: Contains non-sensitive configuration
  - `storage.yaml`: Defines PersistentVolumes and PersistentVolumeClaims for databases
  - `registry-service.yaml`: Service registry (Eureka)
  - `zipkin.yaml`: Distributed tracing (Zipkin, Zipkin Storage, Zipkin Dependencies)
  - `monitoring.yaml`: Monitoring tools (Prometheus, Grafana)
  - `traefik-middleware.yaml`: Defines Traefik middleware for HTTP to HTTPS redirection
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


1. Prepare metallb:
```bash
   kubectl apply -n metallb-system -f metallb-config.yaml
   ```

2. Create the namespace:
   ```bash
   kubectl apply -f namespace.yaml
   ```
   
3. Create local-storage clas
   ```bash
   kubectl apply -n microservices -f local-storageclass.yaml
   ```

2. Create ConfigMap and Secret:
   ```bash
   kubectl apply -n microservices -f configmap.yaml
   kubectl apply -n microservices -f secrets.yaml
   ```

   > **Note**: Before applying `secrets.yaml`, update the placeholder values with actual secrets.

3. Create storage resources:
   ```bash
   kubectl apply -n microservices -f storage.yaml
   ```

4. Deploy infrastructure services:
   ```bash
   kubectl apply -n microservices -f zipkin.yaml
   kubectl apply -n microservices -f registry-service.yaml
   ```

5. Deploy database services:
   ```bash
   kubectl apply -n microservices -f authentication-db.yaml
   kubectl apply -n microservices -f usermanagement-db.yaml
   kubectl apply -n microservices -f chess-db.yaml
   kubectl apply -n microservices -f fitness-db.yaml
   kubectl apply -n microservices -f music-db.yaml
   ```

6. Deploy application services:
   ```bash
   kubectl apply -n microservices -f admin-service.yaml
   kubectl apply -n microservices -f authentication-service.yaml
   kubectl apply -n microservices -f usermanagement-service.yaml
   kubectl apply -n microservices -f website-service.yaml
   kubectl apply -n microservices -f chess-service.yaml
   kubectl apply -n microservices -f fitness-service.yaml
   kubectl apply -n microservices -f music-service.yaml
   ```
```bash
   kubectl apply -n microservices -f prometheus.yaml
   kubectl apply -n microservices -f grafana.yaml
```

7. Deploy gateway service with Ingress:

   ### TLS Certificate

   Before deploying the gateway service, ensure you have created a TLS Secret named `michibaum-tls` in the `microservices` namespace:
   
   ```bash
   k0s kubectl create secret tls michibaum-tls --cert=/data/ssl/fullchain.pem --key=/data/ssl/privkey.pem -n microservices
   ```

   ```bash
   kubectl apply -n microservices -f gateway-service.yaml
   ```
   
8. Deploy DB backups

   ```bash
   kubectl apply -n microservices -f db-backup.yaml
   ```

## Deploy everithing in folder

```bash
   kubectl apply -f <folder>
```

## Ingress Configuration


## Notes

- The configuration uses hostPath volumes for simplicity. In a production environment, you should use a more robust storage solution like a cloud provider's persistent disk.
- Secrets contain placeholder values. Replace them with actual values before deployment.

## Port Forward

```yaml
kubectl port-forward -n microservices svc/authentication-db 3307:3306
kubectl port-forward -n microservices svc/chess-db 3309:3306
kubectl port-forward -n microservices svc/fitness-db 3310:3306
kubectl port-forward -n microservices svc/music-db 3311:3306
kubectl port-forward -n microservices svc/usermanagement-db 3308:3306
```

## Updating the Kubernetes Deployment

### Update Types

Updates to the microservices application can be categorized into several types:

1. **Service Image Updates**: Deploying new versions of service containers
2. **Configuration Updates**: Changes to ConfigMaps or Secrets
3. **Database Schema Changes**: Updates that require database migrations
4. **Infrastructure Updates**: Changes to the underlying Kubernetes resources

### Updating Service Images

#### Using Specific Tags

While the current configuration uses the `latest` tag, it's recommended to use specific version tags for production deployments. This allows for better control over which version is deployed and makes rollbacks easier.

To update a service image:

1. Update the image tag in the service's deployment YAML file:

```yaml
containers:
- name: authentication-service
  image: 70131370/authentication-service:1.2.3  # Use specific version instead of 'latest'
```

2. Apply the updated deployment:

```bash
kubectl apply -n microservices  -f authentication-service.yaml
```

#### Using kubectl set image

For quick updates without modifying YAML files, you can use the `kubectl set image` command:

```bash
kubectl set image deployment/authentication-service authentication-service=70131370/authentication-service:1.2.3 -n microservices
```

#### Monitoring the Update

Monitor the rollout status to ensure the update is proceeding as expected:

```bash
kubectl rollout status deployment/authentication-service -n microservices
```

### Updating Configuration

#### ConfigMap Updates

To update configuration in ConfigMaps:

1. Edit the `configmap.yaml` file with the new configuration values
2. Apply the updated ConfigMap:

```bash
kubectl apply -n microservices -f kubernetes/configmap.yaml
```

3. Restart the affected deployments to pick up the new configuration:

```bash
kubectl rollout restart deployment/authentication-service -n microservices
```

#### Secret Updates

To update sensitive configuration in Secrets:

1. Edit the `secrets.yaml` file with the new secret values
2. Apply the updated Secret:

```bash
kubectl apply -n microservices -f kubernetes/secrets.yaml
```

3. Restart the affected deployments:

```bash
kubectl rollout restart deployment/authentication-service -n microservices
```

### Database Schema Changes

Database schema changes require careful handling to avoid data loss or service disruptions.

#### Recommended Approach

1. **Ensure Backward Compatibility**: Design schema changes to be backward compatible when possible
2. **Create a Migration Plan**: Document the steps required for the migration
3. **Backup Data**: Always backup the database before schema changes

#### Implementation Steps

1. Create a database backup:

```bash
kubectl exec -it <database-pod-name> -n microservices -- mysqldump -u root -p<password> <database-name> > backup.sql
```

2. Apply schema changes using a migration tool or script
3. Update the application to use the new schema
4. Verify the application works correctly with the new schema
5. If issues occur, be prepared to rollback to the previous schema

### Rolling Updates

Kubernetes performs rolling updates by default when you apply changes to a deployment. This ensures that there is no downtime during the update process.

#### Customizing Rolling Update Strategy

You can customize the rolling update strategy in the deployment YAML:

```yaml
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
```

- `maxUnavailable`: Maximum number of pods that can be unavailable during the update
- `maxSurge`: Maximum number of pods that can be created over the desired number of pods

#### Canary Deployments

For critical updates, consider using a canary deployment approach:

1. Deploy a single instance of the new version alongside the existing version
2. Route a small percentage of traffic to the new version
3. Monitor for any issues
4. Gradually increase traffic to the new version
5. Once confident, complete the rollout of the new version

### Rollback Procedures

If an update causes issues, you can rollback to a previous version.

#### Rolling Back a Deployment

```bash
kubectl rollout undo deployment/authentication-service -n microservices
```

To rollback to a specific revision:

```bash
# List revisions
kubectl rollout history deployment/authentication-service -n microservices

# Rollback to a specific revision
kubectl rollout undo deployment/authentication-service --to-revision=2 -n microservices
```

#### Rolling Back Configuration Changes

If a ConfigMap or Secret update causes issues:

1. Reapply the previous version of the ConfigMap or Secret
2. Restart the affected deployments:

```bash
kubectl rollout restart deployment/authentication-service -n microservices
```

### Best Practices

1. **Use Specific Image Tags**: Avoid using `latest` tag in production
3. **Test Updates in Staging**: Always test updates in a staging environment before applying to production
4. **Implement Health Checks**: Ensure all services have proper readiness and liveness probes
5. **Monitor During Updates**: Closely monitor application metrics and logs during updates
6. **Automate Updates**: Use CI/CD pipelines to automate the update process
7. **Document Changes**: Keep a changelog of all updates applied to the cluster
8. **Regular Backups**: Maintain regular backups of all databases and configuration
9. **Update Strategy**: Define an update strategy for each service based on its criticality
10. **Communication**: Inform all stakeholders before performing updates that might affect service availability
