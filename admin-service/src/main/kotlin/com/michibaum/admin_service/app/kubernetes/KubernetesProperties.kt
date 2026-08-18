package com.michibaum.admin_service.app.kubernetes

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kubernetes")
data class KubernetesProperties(
    /**
     * Default namespace to use when no namespace is specified.
     */
    val defaultNamespace: String = "microservices"
)
