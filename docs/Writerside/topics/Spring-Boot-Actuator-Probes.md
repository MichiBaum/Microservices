# Spring Boot Actuator Probes and Kubernetes

This document explains how Spring Boot Actuator health endpoints (`/actuator/health/readiness` and `/actuator/health/liveness`) interact with Kubernetes probes (`readinessProbe`, `livenessProbe`, and `startupProbe`).

## Do Actuator endpoints return objects?
Yes, Spring Boot Actuator health endpoints return **JSON objects**.

Example of a successful response:
```json
{
  "status": "UP",
  "components": {
    "livenessState": {
      "status": "UP"
    }
  }
}
```

## Are these object values checked in Kubernetes?
**No**, Kubernetes (via `httpGet` probes) **does not parse or check the values inside the JSON object**.

Instead, Kubernetes relies on the **HTTP Response Status Code**:
- **Success**: Any status code `>= 200` and `< 400` (e.g., `200 OK`) is considered a pass.
- **Failure**: Any other status code (e.g., `503 Service Unavailable` or `500 Internal Server Error`) is considered a failure.

## How it works together
The integration works because Spring Boot is configured to map its internal health state to HTTP status codes:

1.  **Configuration**: In `application.yml`, probes are enabled:
    ```yaml
    management:
      endpoint:
        health:
          probes:
            enabled: true
      health:
        livenessstate:
          enabled: true
        readinessstate:
          enabled: true
    ```
2.  **Mapping**: 
    - Internal state **UP** maps to **HTTP 200**.
    - Internal state **DOWN** or **OUT_OF_SERVICE** maps to **HTTP 503**.
3.  **Probing**: Kubernetes performs the HTTP request. 
    - A `200 OK` confirms health.
    - A `503 Service Unavailable` triggers failure logic (e.g., container restart or removal from service load balancer).

## Summary
Kubernetes checks the **HTTP status code**, which Spring Boot Actuator manages based on internal application health. The JSON response is intended for observability and monitoring tools, not for Kubernetes' native probing mechanism.
