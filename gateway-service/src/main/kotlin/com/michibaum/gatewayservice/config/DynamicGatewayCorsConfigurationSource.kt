package com.michibaum.gatewayservice.config

import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.cors.CorsConfiguration as SpringCorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import java.util.concurrent.atomic.AtomicReference

@Component
class DynamicGatewayCorsConfigurationSource(
    private val corsProperties: CorsProperties,
    private val serviceGatewayConfigStore: ServiceGatewayConfigStore
) : CorsConfigurationSource {

    private val hostMatcher = AntPathMatcher(".")
    private val snapshotRef = AtomicReference<CompiledCorsSnapshot>(
        compileSnapshot(serviceGatewayConfigStore.currentServices())
    )

    init {
        serviceGatewayConfigStore.registerListener { discoverySnapshot ->
            snapshotRef.set(compileSnapshot(discoverySnapshot.metadataList))
        }
    }

    override fun getCorsConfiguration(request: HttpServletRequest): SpringCorsConfiguration {
        val rawHost = request.getHeader("Host")?.takeIf { it.isNotBlank() } ?: request.serverName
        val normalizedHost = rawHost?.substringBefore(':')?.trim()?.lowercase().orEmpty()
        val activeSnapshot = snapshotRef.get()

        if (normalizedHost.isEmpty()) {
            return activeSnapshot.defaultConfiguration
        }

        val matchedRule = activeSnapshot.rules.firstOrNull { rule ->
            rule.hostPatterns.any { pattern -> hostMatcher.match(pattern, normalizedHost) }
        }
        return matchedRule?.configuration ?: activeSnapshot.defaultConfiguration
    }

    fun rebuild(services: List<GatewayServiceMetadata>) {
        snapshotRef.set(compileSnapshot(services))
    }

    private fun compileSnapshot(services: List<GatewayServiceMetadata>): CompiledCorsSnapshot {
        val baseOrigins = corsProperties.effectiveBaseAllowedOriginPatterns()
        val defaultConfig = SpringCorsConfiguration().apply {
            allowedOriginPatterns = baseOrigins
            allowedMethods = listOf("*")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }

        val rules = services.flatMap { service ->
            val mergedConfig = SpringCorsConfiguration().apply {
                allowedOriginPatterns = (baseOrigins + service.cors.allowedOriginPatterns)
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .distinct()
                allowedMethods = service.cors.allowedMethods.ifEmpty { listOf("*") }
                allowedHeaders = service.cors.allowedHeaders.ifEmpty { listOf("*") }
                allowCredentials = service.cors.allowCredentials
            }

            service.routes
                .filter { it.hosts.isNotEmpty() }
                .map { route ->
                    val normalizedHosts = route.hosts.map { it.trim().lowercase() }.filter { it.isNotEmpty() }
                    CompiledHostCorsRule(
                        serviceId = service.serviceId,
                        hostPatterns = normalizedHosts,
                        order = route.order,
                        specificity = computeHostSpecificity(normalizedHosts),
                        configuration = mergedConfig
                    )
                }
        }.sortedWith(
            compareBy<CompiledHostCorsRule> { it.order }
                .thenByDescending { it.specificity }
                .thenBy { it.serviceId }
        )

        return CompiledCorsSnapshot(
            defaultConfiguration = defaultConfig,
            rules = rules
        )
    }

    private fun computeHostSpecificity(hosts: List<String>): Int {
        if (hosts.isEmpty()) {
            return -1
        }
        return hosts.maxOf { pattern ->
            val firstDynamicIndex = pattern.indexOfFirst { it == '*' || it == '{' || it == '?' }
            val literalPrefix = if (firstDynamicIndex >= 0) pattern.substring(0, firstDynamicIndex) else pattern
            val labelCount = pattern.split('.').count { it.isNotEmpty() && !it.contains('*') && !it.contains('{') }
            (labelCount * 100) + literalPrefix.length
        }
    }

    private data class CompiledCorsSnapshot(
        val defaultConfiguration: SpringCorsConfiguration,
        val rules: List<CompiledHostCorsRule>
    )

    private data class CompiledHostCorsRule(
        val serviceId: String,
        val hostPatterns: List<String>,
        val order: Int,
        val specificity: Int,
        val configuration: SpringCorsConfiguration
    )
}
