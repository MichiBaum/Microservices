FROM alpine AS builder

RUN apk add --no-cache curl
RUN curl -L https://github.com/a8m/envsubst/releases/download/v1.2.0/envsubst-Linux-arm64 -o /envsubst
RUN chmod +x /envsubst

# Copy and set permissions for entrypoint.sh
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

FROM prom/prometheus:v3.2.1

COPY prometheus-template.yml /etc/prometheus/
COPY entrypoint.sh /entrypoint.sh
COPY --from=builder /envsubst /usr/local/bin/envsubst
COPY --from=builder /entrypoint.sh /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]