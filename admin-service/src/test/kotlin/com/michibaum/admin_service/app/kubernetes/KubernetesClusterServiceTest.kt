package com.michibaum.admin_service.app.kubernetes

import io.fabric8.kubernetes.api.model.*
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation
import io.fabric8.kubernetes.client.dsl.PodResource
import io.fabric8.kubernetes.client.dsl.ServiceResource
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException

class KubernetesClusterServiceTest {
    
    private val kubernetesClient = mockk<KubernetesClient>()
    private val kubernetesProperties = KubernetesProperties()
    private val service = KubernetesClusterService(kubernetesClient, kubernetesProperties)

    @Test
    fun `getPods returns mapped PodDto list`() {
        val podsOperation = mockk<MixedOperation<Pod, PodList, PodResource>>()
        val podNamespaceOperation = mockk<NonNamespaceOperation<Pod, PodList, PodResource>>()
        val podList = mockk<PodList>()

        val pod = Pod().apply {
            metadata = ObjectMeta().apply {
                name = "test-pod"
                namespace = "microservices"
                creationTimestamp = "2026-08-05T09:00:00Z"
            }
            status = PodStatus().apply {
                phase = "Running"
                podIP = "10.244.0.5"
            }
            spec = PodSpec().apply {
                nodeName = "node-1"
                containers = listOf(Container().apply { name = "app" })
            }
        }

        every { kubernetesClient.namespace } returns "microservices"
        every { kubernetesClient.pods() } returns podsOperation
        every { podsOperation.inNamespace("microservices") } returns podNamespaceOperation
        every { podNamespaceOperation.list() } returns podList
        every { podList.items } returns listOf(pod)

        val result = service.getPods()

        assertEquals(1, result.size)
        val podDto = result.first()
        assertEquals("test-pod", podDto.name)
        assertEquals("microservices", podDto.namespace)
        assertEquals("Running", podDto.status)
        assertEquals("10.244.0.5", podDto.podIp)
        assertEquals("node-1", podDto.nodeName)
        assertEquals(listOf("app"), podDto.containers)
    }

    @Test
    fun `getServices returns mapped ServiceDto list`() {
        val servicesOperation = mockk<MixedOperation<Service, ServiceList, ServiceResource<Service>>>()
        val serviceNamespaceOperation = mockk<NonNamespaceOperation<Service, ServiceList, ServiceResource<Service>>>()
        val serviceList = mockk<ServiceList>()

        val k8sService = Service().apply {
            metadata = ObjectMeta().apply {
                name = "test-service"
                namespace = "microservices"
            }
            spec = ServiceSpec().apply {
                type = "ClusterIP"
                clusterIP = "10.96.0.10"
                ports = listOf(ServicePort().apply {
                    name = "http"
                    port = 80
                    protocol = "TCP"
                })
                selector = mapOf("app" to "test-service")
            }
        }

        every { kubernetesClient.namespace } returns "microservices"
        every { kubernetesClient.services() } returns servicesOperation
        every { servicesOperation.inNamespace("microservices") } returns serviceNamespaceOperation
        every { serviceNamespaceOperation.list() } returns serviceList
        every { serviceList.items } returns listOf(k8sService)

        val result = service.getServices()

        assertEquals(1, result.size)
        val serviceDto = result.first()
        assertEquals("test-service", serviceDto.name)
        assertEquals("ClusterIP", serviceDto.type)
        assertEquals("10.96.0.10", serviceDto.clusterIp)
        assertEquals(1, serviceDto.ports.size)
        assertEquals(80, serviceDto.ports.first().port)
    }

    @Test
    fun `getPods throws 503 when Kubernetes API server fails`() {
        every { kubernetesClient.namespace } returns "microservices"
        every { kubernetesClient.pods() } throws RuntimeException("Connection refused")

        assertThrows<ResponseStatusException> {
            service.getPods()
        }
    }

    @Test
    fun `getPods throws 503 when kubernetesClient is null`() {
        val nullClientService = KubernetesClusterService(null, kubernetesProperties)

        assertThrows<ResponseStatusException> {
            nullClientService.getPods()
        }
    }

    @Test
    fun `getServices throws 503 when kubernetesClient is null`() {
        val nullClientService = KubernetesClusterService(null, kubernetesProperties)

        assertThrows<ResponseStatusException> {
            nullClientService.getServices()
        }
    }

    @Test
    fun `getServiceHealth returns UP when all pods are ready`() {
        val servicesOperation = mockk<MixedOperation<Service, ServiceList, ServiceResource<Service>>>()
        val serviceNamespaceOperation = mockk<NonNamespaceOperation<Service, ServiceList, ServiceResource<Service>>>()
        val serviceResource = mockk<ServiceResource<Service>>()
        val k8sService = Service().apply {
            spec = ServiceSpec().apply {
                selector = mapOf("app" to "test-db")
            }
        }

        val podsOperation = mockk<MixedOperation<Pod, PodList, PodResource>>()
        val podNamespaceOperation = mockk<NonNamespaceOperation<Pod, PodList, PodResource>>()
        val podList = mockk<PodList>()
        val pod = Pod().apply {
            metadata = ObjectMeta().apply { name = "pod-1" }
            status = PodStatus().apply {
                phase = "Running"
                containerStatuses = listOf(ContainerStatus().apply { ready = true })
            }
        }

        every { kubernetesClient.namespace } returns "microservices"
        every { kubernetesClient.services() } returns servicesOperation
        every { servicesOperation.inNamespace("microservices") } returns serviceNamespaceOperation
        every { serviceNamespaceOperation.withName("test-db") } returns serviceResource
        every { serviceResource.get() } returns k8sService

        every { kubernetesClient.pods() } returns podsOperation
        every { podsOperation.inNamespace("microservices") } returns podNamespaceOperation
        every { podNamespaceOperation.withLabels(mapOf("app" to "test-db")) } returns podNamespaceOperation
        every { podNamespaceOperation.list() } returns podList
        every { podList.items } returns listOf(pod)

        val health = service.getServiceHealth("test-db")

        assertEquals("UP", health.status)
        assertEquals(1, health.details["totalPods"])
    }
}
