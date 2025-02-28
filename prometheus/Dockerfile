FROM prom/prometheus

RUN apk add --no-cache gettext
COPY prometheus-template.yml /etc/prometheus
ENTRYPOINT envsubst < /etc/prometheus/prometheus-template.yml > /etc/prometheus/prometheus.yml && /bin/prometheus --config.file=/etc/prometheus/prometheus.yml