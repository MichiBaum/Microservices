package com.michibaum.admin_service.config

import com.michibaum.admin_service.app.kubernetes.KubernetesClusterService
import com.michibaum.admin_service.app.kubernetes.dto.ServiceHealthDto
import de.codecentric.boot.admin.server.config.AdminServerProperties
import de.codecentric.boot.admin.server.domain.entities.Instance
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import de.codecentric.boot.admin.server.domain.values.InstanceId
import de.codecentric.boot.admin.server.domain.values.Registration
import de.codecentric.boot.admin.server.web.client.InstanceWebClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import reactor.core.publisher.Mono

class ExternalServiceHealthConfigurationTest {

    private val kubernetesClusterService = mockk<KubernetesClusterService>()
    private val adminServerProperties = AdminServerProperties()
    private val configuration = ExternalServiceHealthConfiguration(kubernetesClusterService, adminServerProperties)

    private fun createInstance(
        name: String,
        metadata: Map<String, String> = emptyMap(),
    ): Instance {
        val builder = Registration.create(name, "http://localhost:8080/actuator/health")
        metadata.forEach { (k, v) -> builder.metadata(k, v) }
        return Instance.create(InstanceId.of("$name-id")).register(builder.build())
    }

    @ParameterizedTest
    @ValueSource(strings = ["jaeger", "jaeger-storage", "prometheus", "otel-collector", "grafana", "fitness-db", "chess-db"])
    fun `identifies observability and database services as external`(serviceName: String) {
        val instance = createInstance(serviceName)
        assertTrue(configuration.isExternalService(instance))
    }

    @Test
    fun `identifies service with sba-check kubernetes metadata as external`() {
        val instance = createInstance("custom-non-spring-app", mapOf("sba-check" to "kubernetes"))
        assertTrue(configuration.isExternalService(instance))
    }

    @ParameterizedTest
    @ValueSource(strings = ["admin-service", "chess-service", "gateway-service", "usermanagement-service"])
    fun `does not identify regular Spring Boot microservices as external`(serviceName: String) {
        val instance = createInstance(serviceName)
        assertFalse(configuration.isExternalService(instance))
    }

    @Test
    fun `endpointDetector skips HTTP endpoint probing for otel-collector and grafana`() {
        val otelInstance = createInstance("otel-collector")
        val grafanaInstance = createInstance("grafana")

        val repository = mockk<InstanceRepository>()
        every { repository.find(otelInstance.id) } returns Mono.just(otelInstance)
        every { repository.find(grafanaInstance.id) } returns Mono.just(grafanaInstance)

        val instanceWebClientBuilder = mockk<InstanceWebClient.Builder>()
        val instanceWebClient = mockk<InstanceWebClient>(relaxed = true)
        every { instanceWebClientBuilder.build() } returns instanceWebClient

        val detector = configuration.endpointDetector(repository, instanceWebClientBuilder)

        assertNull(detector.detectEndpoints(otelInstance.id).block())
        assertNull(detector.detectEndpoints(grafanaInstance.id).block())
        verify(exactly = 0) { instanceWebClient.instance(any<Instance>()) }
    }

    @Test
    fun `statusUpdater delegates otel-collector and grafana health to KubernetesClusterService`() {
        val otelInstance = createInstance("otel-collector")
        every { kubernetesClusterService.getServiceHealth("otel-collector") } returns
            ServiceHealthDto("UP", mapOf("readyPods" to 1))

        var savedInstance: Instance? = null
        val repository = mockk<InstanceRepository>()
        every { repository.computeIfPresent(otelInstance.id, any()) } answers {
            val remapping = secondArg<java.util.function.BiFunction<InstanceId, Instance, Mono<Instance>>>()
            remapping.apply(otelInstance.id, otelInstance).doOnNext { savedInstance = it }
        }

        val instanceWebClientBuilder = mockk<InstanceWebClient.Builder>()
        every { instanceWebClientBuilder.build() } returns mockk(relaxed = true)

        val statusUpdater = configuration.statusUpdater(repository, instanceWebClientBuilder, mockk(relaxed = true))
        statusUpdater.updateStatus(otelInstance.id).block()

        assertEquals("UP", savedInstance?.statusInfo?.status)
        verify(exactly = 1) { kubernetesClusterService.getServiceHealth("otel-collector") }
    }
}
