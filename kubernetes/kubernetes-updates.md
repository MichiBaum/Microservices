# Kubernetes Updates Guide

This document provides guidelines and procedures for updating the microservices application running on Kubernetes.

## Table of Contents

- [Overview](#overview)
- [Update Types](#update-types)
- [Updating Service Images](#updating-service-images)
- [Updating Configuration](#updating-configuration)
- [Database Schema Changes](#database-schema-changes)
- [Rolling Updates](#rolling-updates)
- [Rollback Procedures](#rollback-procedures)
- [Best Practices](#best-practices)

## Overview

Updating applications in Kubernetes requires careful planning and execution to minimize downtime and avoid service disruptions. This guide covers different types of updates and the recommended procedures for each.

## Update Types

Updates to the microservices application can be categorized into several types:

1. **Service Image Updates**: Deploying new versions of service containers
2. **Configuration Updates**: Changes to ConfigMaps or Secrets
3. **Database Schema Changes**: Updates that require database migrations
4. **Infrastructure Updates**: Changes to the underlying Kubernetes resources

## Updating Service Images

### Using Specific Tags

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
kubectl apply -f kubernetes/authentication-service.yaml
```

### Using kubectl set image

For quick updates without modifying YAML files, you can use the `kubectl set image` command:

```bash
kubectl set image deployment/authentication-service authentication-service=70131370/authentication-service:1.2.3 -n microservices
```

### Monitoring the Update

Monitor the rollout status to ensure the update is proceeding as expected:

```bash
kubectl rollout status deployment/authentication-service -n microservices
```

## Updating Configuration

### ConfigMap Updates

To update configuration in ConfigMaps:

1. Edit the `configmap.yaml` file with the new configuration values
2. Apply the updated ConfigMap:

```bash
kubectl apply -f kubernetes/configmap.yaml
```

3. Restart the affected deployments to pick up the new configuration:

```bash
kubectl rollout restart deployment/authentication-service -n microservices
```

### Secret Updates

To update sensitive configuration in Secrets:

1. Edit the `secrets.yaml` file with the new secret values
2. Apply the updated Secret:

```bash
kubectl apply -f kubernetes/secrets.yaml
```

3. Restart the affected deployments:

```bash
kubectl rollout restart deployment/authentication-service -n microservices
```

## Database Schema Changes

Database schema changes require careful handling to avoid data loss or service disruptions.

### Recommended Approach

1. **Ensure Backward Compatibility**: Design schema changes to be backward compatible when possible
2. **Create a Migration Plan**: Document the steps required for the migration
3. **Backup Data**: Always backup the database before schema changes

### Implementation Steps

1. Create a database backup:

```bash
kubectl exec -it <database-pod-name> -n microservices -- mysqldump -u root -p<password> <database-name> > backup.sql
```

2. Apply schema changes using a migration tool or script
3. Update the application to use the new schema
4. Verify the application works correctly with the new schema
5. If issues occur, be prepared to rollback to the previous schema

## Rolling Updates

Kubernetes performs rolling updates by default when you apply changes to a deployment. This ensures that there is no downtime during the update process.

### Customizing Rolling Update Strategy

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

### Canary Deployments

For critical updates, consider using a canary deployment approach:

1. Deploy a single instance of the new version alongside the existing version
2. Route a small percentage of traffic to the new version
3. Monitor for any issues
4. Gradually increase traffic to the new version
5. Once confident, complete the rollout of the new version

## Rollback Procedures

If an update causes issues, you can rollback to a previous version.

### Rolling Back a Deployment

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

### Rolling Back Configuration Changes

If a ConfigMap or Secret update causes issues:

1. Reapply the previous version of the ConfigMap or Secret
2. Restart the affected deployments

## Best Practices

1. **Use Specific Image Tags**: Avoid using `latest` tag in production
2. **Version Control Configuration**: Keep all Kubernetes YAML files in version control
3. **Test Updates in Staging**: Always test updates in a staging environment before applying to production
4. **Implement Health Checks**: Ensure all services have proper readiness and liveness probes
5. **Monitor During Updates**: Closely monitor application metrics and logs during updates
6. **Automate Updates**: Use CI/CD pipelines to automate the update process
7. **Document Changes**: Keep a changelog of all updates applied to the cluster
8. **Regular Backups**: Maintain regular backups of all databases and configuration
9. **Update Strategy**: Define an update strategy for each service based on its criticality
10. **Communication**: Inform all stakeholders before performing updates that might affect service availability