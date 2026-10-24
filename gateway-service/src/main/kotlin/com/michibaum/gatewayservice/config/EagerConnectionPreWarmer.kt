package com.michibaum.gatewayservice.config

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.concurrent.CompletableFuture

/**
 * Pre-warms HTTP connection pools to downstream microservices and external endpoints
 * upon application startup. This pre-allocates idle TCP/HTTP connections before user
 * requests arrive, preventing cold-start timeouts in gateway routing.
 */
@Component
class EagerConnectionPreWarmer(
    private val discoveryClient: DiscoveryClient,
    private val restTemplate: RestTemplate,
    private val servicesProperties: ServicesProperties
) {

    private val logger = LoggerFactory.getLogger(EagerConnectionPreWarmer::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        CompletableFuture.runAsync {
            preWarmConnections()
        }
    }

    fun preWarmConnections() {
        logger.info("Starting eager connection pool pre-warming for registered services...")

        for (service in Service.entries) {
            try {
                when (service) {
                    Service.JAEGER -> warmUrl(servicesProperties.jaegerUrl.resolve("/actuator/health/liveness").toString(), service.id)
                    Service.GRAFANA -> warmUrl(servicesProperties.grafanaUrl.resolve("/actuator/health/liveness").toString(), service.id)
                    Service.PROMETHEUS -> warmUrl(servicesProperties.prometheusUrl.resolve("/actuator/health/liveness").toString(), service.id)
                    else -> {
                        val instances = discoveryClient.getInstances(service.id)
                        if (instances.isNotEmpty()) {
                            for (instance in instances) {
                                val url = "${instance.uri}/actuator/health/liveness"
                                warmUrl(url, service.id)
                            }
                        } else {
                            logger.debug("No discovery instances found to pre-warm for service {}", service.id)
                        }
                    }
                }
            } catch (e: Exception) {
                logger.warn("Failed to pre-warm connection for service {}: {}", service.id, e.message)
            }
        }

        logger.info("Eager connection pool pre-warming completed.")
    }

    private fun warmUrl(url: String, serviceId: String) {
        try {
            logger.debug("Pre-warming connection to {} ({})", serviceId, url)
            restTemplate.getForEntity(url, String::class.java)
        } catch (e: Exception) {
            logger.debug("Pre-warm request to {} ({}) returned error: {}", serviceId, url, e.message)
        }
    }
}
