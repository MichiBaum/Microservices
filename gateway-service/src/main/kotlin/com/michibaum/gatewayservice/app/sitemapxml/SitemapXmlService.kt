package com.michibaum.gatewayservice.app.sitemapxml

import com.michibaum.authentication_starter.AdminServiceCredentials
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import com.michibaum.gateway_discovery.sitemap.SitemapUrlsResponse
import com.michibaum.gatewayservice.config.ServiceGatewayConfigStore
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.annotation.Observed
import org.apache.commons.logging.LogFactory
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient
import org.springframework.core.task.VirtualThreadTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.net.URI
import java.util.concurrent.Callable

@Service
class SitemapXmlService(
    private val sitemapXmlProperties: SitemapXmlProperties,
    private val serviceGatewayConfigStore: ServiceGatewayConfigStore,
    private val loadBalancerClient: LoadBalancerClient,
    restClientBuilder: RestClient.Builder,
    private val adminServiceCredentials: AdminServiceCredentials,
    private val observationRegistry: ObservationRegistry
) {

    private val log = LogFactory.getLog(SitemapXmlService::class.java)
    private val restClient: RestClient = restClientBuilder.build()
    private val taskExecutor = VirtualThreadTaskExecutor("sitemap-data-loader-")

    @Observed(name = "sitemap-xml-service-generate")
    fun generateWith(host: String): String {
        observationRegistry.currentObservation?.highCardinalityKeyValue("host", host)

        // Sitemap size limits: All formats limit a single sitemap to 50MB (uncompressed) or 50,000 URLs.
        val protocol = if (sitemapXmlProperties.https) "https://" else "http://"
        val baseUrl = "$protocol$host"

        val discoveredServices = serviceGatewayConfigStore.currentServices().sortedBy { it.serviceId }

        val staticPaths = buildList {
            addAll(sitemapXmlProperties.locations)
            discoveredServices.forEach { service ->
                addAll(service.sitemap.staticLocations)
            }
        }

        val dynamicPaths = discoveredServices
            .filter { !it.sitemap.endpointPath.isNullOrBlank() }
            .map { service ->
                taskExecutor.submitCompletable(Callable {
                    fetchDynamicServiceUrls(service)
                })
            }
            .flatMap { it.join() }

        val allNormalizedLocations = (staticPaths + dynamicPaths)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { pathOrUrl ->
                if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
                    pathOrUrl
                } else {
                    val normalizedPath = if (pathOrUrl.startsWith("/")) pathOrUrl else "/$pathOrUrl"
                    "$baseUrl$normalizedPath"
                }
            }
            .distinct()

        val xmlBuilder = StringBuilder()
        xmlBuilder.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        xmlBuilder.appendLine("""<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">""")

        allNormalizedLocations.forEach { fullLoc ->
            xmlBuilder.appendLine(
                """
                <url>
                    <loc>$fullLoc</loc>
                </url>
                """.trimIndent()
            )
        }

        xmlBuilder.appendLine("</urlset>")
        return xmlBuilder.toString()
    }

    private fun fetchDynamicServiceUrls(service: GatewayServiceMetadata): List<String> {
        val endpointPath = service.sitemap.endpointPath?.trim()?.takeIf { it.isNotEmpty() } ?: return emptyList()
        val observation = Observation.start("sitemap-xml-data-fetch", observationRegistry)
        observation.lowCardinalityKeyValue("sitemap.service.id", service.serviceId)
        return try {
            val instance: org.springframework.cloud.client.ServiceInstance? = loadBalancerClient.choose(service.serviceId)
            if (instance == null) {
                log.warn("Skipping dynamic sitemap fetch for '${service.serviceId}': no live ServiceInstance available")
                return emptyList()
            }

            val normalizedEndpoint = if (endpointPath.startsWith("/")) endpointPath else "/$endpointPath"
            val targetUri = URI.create("${instance.uri}$normalizedEndpoint")

            val response = restClient.get()
                .uri(targetUri)
                .headers { headers ->
                    headers.setBasicAuth(adminServiceCredentials.username, adminServiceCredentials.password)
                }
                .retrieve()
                .body(SitemapUrlsResponse::class.java)

            response?.urls.orEmpty()
        } catch (ex: Exception) {
            observation.error(ex)
            log.warn("Failed to fetch dynamic sitemap URLs from service '${service.serviceId}': ${ex.message}")
            emptyList()
        } finally {
            observation.stop()
        }
    }
}