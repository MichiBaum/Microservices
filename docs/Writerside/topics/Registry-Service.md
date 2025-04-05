# Registry Service

The Registry Service is a microservice built with Spring Boot that provides service discovery and registration for the microservices architecture. It allows services to dynamically discover and communicate with each other without hardcoded configurations.

## Key Features

- **Service Registration**: Allows microservices to register themselves when they start up.
- **Service Discovery**: Enables microservices to discover and locate other services dynamically.
- **Health Monitoring**: Tracks the health and availability of registered services.
- **Load Balancing**: Provides information for client-side load balancing.
- **Failover Support**: Helps with failover by maintaining information about available service instances.

## How It Works

1. When a microservice starts, it registers itself with the Registry Service.
2. The Registry Service maintains a registry of all available service instances.
3. Microservices query the Registry Service to discover the locations of other services they need to communicate with.
4. The Registry Service periodically checks the health of registered services and removes unhealthy instances.

## Benefits

- **Decoupling**: Services don't need to know the exact location of other services.
- **Scalability**: New service instances can be added dynamically without configuration changes.
- **Resilience**: The system can adapt to service failures by routing requests to healthy instances.
- **Simplified Configuration**: Reduces the need for hardcoded service URLs and ports.

## Technologies

- Spring Boot
- Spring Cloud Netflix Eureka or Spring Cloud Consul
- RESTful APIs
- Docker for containerization
