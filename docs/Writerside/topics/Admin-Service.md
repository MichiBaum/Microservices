# Admin Service

The Admin Service is a microservice built with Kotlin and Spring Boot that provides administrative functionality for managing and monitoring the entire microservices ecosystem. It offers a centralized interface for administrators to configure, monitor, and maintain the system.

## Key Features

- **Service Monitoring**: Tracks the health, performance, and status of all microservices.
- **Kubernetes Cluster Inspection**: Communicates with the Kubernetes API Server to inspect running Pods and Services.
- **Configuration Management**: Provides interfaces for updating service configurations.
- **User Administration**: Manages administrative users and their permissions.
- **System Metrics**: Collects and displays metrics about system performance and usage.
- **Logging and Auditing**: Centralizes logs and audit trails from all services.
- **Alerting**: Sends notifications when issues are detected.
- **Deployment Management**: Facilitates deployment and updates of services.

## Kubernetes Integration

The Admin Service directly integrates with the Kubernetes API Server using `spring-cloud-starter-kubernetes-fabric8` to provide real-time cluster state visibility.

### REST Endpoints
- `GET /api/k8s/pods`: Returns a list of active Pods in the target namespace, including metadata such as pod status, pod IP, node name, creation timestamp, and container lists.
- `GET /api/k8s/services`: Returns a list of active Services in the target namespace, including service type, cluster IP, port configurations, and selectors.

Both endpoints require authentication and authorization with the `ADMIN_SERVICE` permission.

### RBAC Configuration

To interact with the Kubernetes API Server securely, `admin-service` uses a dedicated Role-Based Access Control (RBAC) setup defined in `kubernetes/microservices/admin-service-rbac.yaml`:

- **ServiceAccount** (`admin-service-sa`): Assigned to the `admin-service` Pod deployment (`serviceAccountName: admin-service-sa`).
- **Role** (`admin-service-role`): Defines namespace-scoped permissions in the `microservices` namespace. Grants `get`, `list`, `watch`, `create`, and `delete` verbs on core resources (`pods`, `services`) and `apps` resources (`deployments`).
- **RoleBinding** (`admin-service-rolebinding`): Binds `admin-service-sa` to `admin-service-role` within the `microservices` namespace.

**Purpose**: Enforces least privilege security containment by restricting `admin-service` privileges strictly to the target `microservices` namespace, preventing unauthorized cluster-wide administrative operations while enabling necessary monitoring capabilities.

## Administrative Dashboard

The Admin Service provides a web-based dashboard that offers:

- Visual representation of system health and metrics
- User-friendly interfaces for common administrative tasks
- Access to logs and audit trails
- Configuration management tools
- User and permission management

## Integration with Other Services

- Communicates with all microservices for monitoring and management
- Integrates with the Kubernetes API Server for workload inspection
- Integrates with the Registry Service for service discovery
- Works with the Authentication Service for admin authentication and authorization

## Security

- Strict access controls for administrative functions
- Role-based permissions for different administrative tasks
- Secure communication with other services
- Namespace-scoped RBAC permissions for Kubernetes API interactions
- Audit logging of all administrative actions

## Technologies

- Kotlin
- Spring Boot
- Spring Cloud Kubernetes (Fabric8)
- Spring Boot Admin or similar monitoring tools
- RESTful APIs
- Docker for containerization
- Kubernetes RBAC
