#!/bin/sh
set -e

# Substitute environment vars at runtime
envsubst -i /etc/prometheus/prometheus-template.yml -o /etc/prometheus/prometheus.yml

# Start Prometheus
exec /bin/prometheus --config.file=/etc/prometheus/prometheus.yml