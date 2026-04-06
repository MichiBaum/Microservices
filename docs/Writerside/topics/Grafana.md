# Grafana

Grafana is used to visualize metrics collected by Prometheus and traces from Jaeger.

## Configuration

The Grafana configuration is defined in `kubernetes/microservices/grafana.yaml`.

### Persistence

Grafana uses a SQLite database to store users, dashboards, and alerts. This database is persisted using a `PersistentVolume` and `PersistentVolumeClaim` mounted at `/var/lib/grafana`.

On the host, the data is stored in `/data/grafana`.

### Provisioning

- **Datasources**: Automatically provisioned from the `grafana-datasources` ConfigMap. Currently, Prometheus and Jaeger are configured.
- **Dashboards**: Dashboards are provisioned from the `grafana-dashboards` ConfigMap (which contains the provider configuration) and look for JSON files in `/var/lib/grafana/dashboards`.

## Troubleshooting

### Database is locked

If you see `database is locked` errors in the logs, it might be due to SQLite locking issues on certain filesystems or multiple processes trying to access the same database file.

To mitigate this, the following environment variables are set:
- `GF_DATABASE_WAL: "false"`: Disables Write-Ahead Logging.
- `GF_DATABASE_BUSY_TIMEOUT: "10000"`: Increases the busy timeout to 10 seconds.

Additionally, ensure that the `securityContext` is correctly set to UID/GID `472` for both the pod and the volume to allow Grafana to have the necessary permissions.

### Outdated provisioning config

If you see warnings about deprecated provisioning config, ensure that the `datasource.yml` and `dashboard.yml` in the ConfigMaps include `apiVersion: 1`.

### Network/DNS Timeouts

If you see errors like `lookup grafana.com: i/o timeout` or `context deadline exceeded`, it means the Grafana pod cannot reach the internet to check for updates or download plugins.

To fix this, disable external checks in the environment variables:
- `GF_ANALYTICS_CHECK_FOR_UPDATES: "false"`

### Gateway Service (503 / Request Aborted)

If the `gateway-service` logs show `ResourceAccessException: I/O error on GET request for "http://grafana:3000/": Request aborted`, it is likely because Grafana is unresponsive due to the "Database is locked" issue. 

When Grafana is stuck in a database lock loop, its web server cannot start, causing the gateway's circuit breaker or connection timeout to trigger and abort the request.

### Unified Alerting

The log message `Running in alternative execution of Error/NoData mode` is related to the Unified Alerting state manager and is generally an info message, but can be triggered more frequently if there are underlying database issues.