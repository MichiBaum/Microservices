package com.michibaum.admin_service.config

import com.michibaum.admin_service.app.kubernetes.KubernetesClusterService
import de.codecentric.boot.admin.server.domain.entities.Instance
import de.codecentric.boot.admin.server.domain.values.Registration
import de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunction
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import java.net.URI

class HealthConfigurationTest {

    private val kubernetesClusterService = mockk<KubernetesClusterService>()
    private val externalConfig = ExternalServiceHealthConfiguration(kubernetesClusterService)

    @Test
    fun `externalServiceHealthFilter intercepts health requests for databases`() {
        val filter = externalConfig.externalServiceHealthFilter()
        val instance = mockk<Instance>()
        val registration = mockk<Registration>()
        val request = mockk<ClientRequest>()
        val next = mockk<ExchangeFunction>()

        every { instance.registration } returns registration
        every { registration.name } returns "test-db"
        every { request.url() } returns URI.create("http://test-db/actuator/health")
        every { kubernetesClusterService.isServiceUp("test-db") } returns true

        val responseMono = filter.filter(instance, request, next)
        val response = responseMono.block()!!

        assertEquals(200, response.statusCode().value())
        val body = response.bodyToMono(String::class.java).block()
        assertEquals("{\"status\":\"UP\"}", body)
    }

    @Test
    fun `externalServiceHealthFilter does NOT intercept non-health requests`() {
        val filter = externalConfig.externalServiceHealthFilter()
        val instance = mockk<Instance>()
        val registration = mockk<Registration>()
        val request = mockk<ClientRequest>()
        val next = mockk<ExchangeFunction>()
        val expectedResponse = mockk<ClientResponse>()

        every { instance.registration } returns registration
        every { registration.name } returns "test-db"
        every { request.url() } returns URI.create("http://test-db/info")
        every { next.exchange(request) } returns Mono.just(expectedResponse)

        val responseMono = filter.filter(instance, request, next)
        val response = responseMono.block()!!

        assertEquals(expectedResponse, response)
    }

    @Test
    fun `externalServiceHealthFilter intercepts health requests for observability`() {
        val filter = externalConfig.externalServiceHealthFilter()
        val instance = mockk<Instance>()
        val registration = mockk<Registration>()
        val request = mockk<ClientRequest>()
        val next = mockk<ExchangeFunction>()

        every { instance.registration } returns registration
        every { registration.name } returns "jaeger"
        every { request.url() } returns URI.create("http://jaeger/health")
        every { kubernetesClusterService.isServiceUp("jaeger") } returns true

        val responseMono = filter.filter(instance, request, next)
        val response = responseMono.block()!!

        assertEquals(200, response.statusCode().value())
        val body = response.bodyToMono(String::class.java).block()
        assertEquals("{\"status\":\"UP\"}", body)
    }
}
