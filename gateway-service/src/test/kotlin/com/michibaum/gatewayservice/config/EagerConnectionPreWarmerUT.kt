package com.michibaum.gatewayservice.config

import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.contains
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.net.URI

class EagerConnectionPreWarmerUT {

    @Test
    fun `preWarmConnections sends requests to discovered instances and external services`() {
        val discoveryClient = mock(DiscoveryClient::class.java)
        val restTemplate = mock(RestTemplate::class.java)
        val servicesProperties = ServicesProperties(
            jaegerUrl = URI.create("http://jaeger:16686"),
            grafanaUrl = URI.create("http://grafana:3000"),
            prometheusUrl = URI.create("http://prometheus:9090")
        )

        val chessInstance = mock(ServiceInstance::class.java)
        `when`(chessInstance.uri).thenReturn(URI.create("http://chess-service:8080"))
        `when`(discoveryClient.getInstances("chess-service")).thenReturn(listOf(chessInstance))

        `when`(restTemplate.getForEntity(contains("/actuator/health/liveness"), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok("OK"))

        val preWarmer = EagerConnectionPreWarmer(discoveryClient, restTemplate, servicesProperties)

        preWarmer.preWarmConnections()

        verify(restTemplate).getForEntity("http://chess-service:8080/actuator/health/liveness", String::class.java)
        verify(restTemplate, atLeastOnce()).getForEntity(contains("http://jaeger:16686"), eq(String::class.java))
        verify(restTemplate, atLeastOnce()).getForEntity(contains("http://grafana:3000"), eq(String::class.java))
        verify(restTemplate, atLeastOnce()).getForEntity(contains("http://prometheus:9090"), eq(String::class.java))
    }

    @Test
    fun `preWarmConnections handles exceptions gracefully when service request fails`() {
        val discoveryClient = mock(DiscoveryClient::class.java)
        val restTemplate = mock(RestTemplate::class.java)
        val servicesProperties = ServicesProperties(
            jaegerUrl = URI.create("http://jaeger:16686"),
            grafanaUrl = URI.create("http://grafana:3000"),
            prometheusUrl = URI.create("http://prometheus:9090")
        )

        val musicInstance = mock(ServiceInstance::class.java)
        `when`(musicInstance.uri).thenReturn(URI.create("http://music-service:8081"))
        `when`(discoveryClient.getInstances("music-service")).thenReturn(listOf(musicInstance))

        `when`(restTemplate.getForEntity("http://music-service:8081/actuator/health/liveness", String::class.java))
            .thenThrow(RuntimeException("Connection refused"))

        val preWarmer = EagerConnectionPreWarmer(discoveryClient, restTemplate, servicesProperties)

        preWarmer.preWarmConnections()

        verify(restTemplate).getForEntity("http://music-service:8081/actuator/health/liveness", String::class.java)
    }
}
