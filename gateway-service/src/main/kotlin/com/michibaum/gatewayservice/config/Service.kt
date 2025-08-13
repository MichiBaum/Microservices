package com.michibaum.gatewayservice.config

enum class Service(val cbId: String, val id: String) {
    GRAFANA("grafana-circuit-breaker", "grafana-service"),
    ZIPKIN("zipkin-circuit-breaker", "zipkin-service"),
    ADMIN("admin-circuit-breaker", "admin-service"),
    PROMETHEUS("prometheus-circuit-breaker", "prometheus-service"),
    AUTHENTICATION("authentication-circuit-breaker", "authentication-service"),
    CHESS("chess-circuit-breaker", "chess-service"),
    USERMANAGEMENT("usermanagement-circuit-breaker", "usermanagement-service"),
    FITNESS("fitness-circuit-breaker", "fitness-service"),
    REGISTRY("registry-circuit-breaker", "registry-service"),
    MUSIC("music-circuit-breaker", "music-service"),
    BABYMETAL("babymetal-circuit-breaker", "babymetal-service"),
    WEBSITE("website-circuit-breaker", "website-service");
}