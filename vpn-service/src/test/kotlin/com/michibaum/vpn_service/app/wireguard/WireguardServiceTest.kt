package com.michibaum.vpn_service.app.wireguard

import com.michibaum.vpn_service.app.kubernetes.KubernetesProperties
import io.fabric8.kubernetes.api.model.Container
import io.fabric8.kubernetes.api.model.ObjectMeta
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.api.model.PodList
import io.fabric8.kubernetes.api.model.PodSpec
import io.fabric8.kubernetes.api.model.PodTemplateSpec
import io.fabric8.kubernetes.api.model.Service
import io.fabric8.kubernetes.api.model.ServiceList
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.fabric8.kubernetes.api.model.apps.DeploymentList
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.CopyOrReadable
import io.fabric8.kubernetes.client.dsl.MixedOperation
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation
import io.fabric8.kubernetes.client.dsl.PodResource
import io.fabric8.kubernetes.client.dsl.RollableScalableResource
import io.fabric8.kubernetes.client.dsl.ServiceResource
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException

class WireguardServiceTest {

    private val kubernetesClient = mockk<KubernetesClient>()
    private val kubernetesProperties = KubernetesProperties()
    private val service = WireguardService(kubernetesClient, kubernetesProperties)

    @Test
    fun `createDeployment throws 503 when kubernetesClient is null`() {
        val nullClientService = WireguardService(null, kubernetesProperties)

        assertThrows<ResponseStatusException> {
            nullClientService.createDeployment("testuser")
        }
    }

    @Test
    fun `createDeployment creates deployment and service successfully`() {
        val deploymentsOperation = mockk<MixedOperation<Deployment, DeploymentList, RollableScalableResource<Deployment>>>()
        val deploymentNamespaceOperation = mockk<NonNamespaceOperation<Deployment, DeploymentList, RollableScalableResource<Deployment>>>()
        val deploymentResource = mockk<RollableScalableResource<Deployment>>()

        val servicesOperation = mockk<MixedOperation<Service, ServiceList, ServiceResource<Service>>>()
        val serviceNamespaceOperation = mockk<NonNamespaceOperation<Service, ServiceList, ServiceResource<Service>>>()
        val serviceResource = mockk<ServiceResource<Service>>()

        val createdDeployment = Deployment().apply {
            metadata = ObjectMeta().apply {
                name = "wireguard-testuser"
                namespace = "microservices"
            }
            spec = DeploymentSpec().apply {
                replicas = 1
                template = PodTemplateSpec().apply {
                    spec = PodSpec().apply {
                        containers = listOf(Container().apply { name = "wireguard" })
                    }
                }
            }
            status = DeploymentStatus().apply {
                readyReplicas = 1
            }
        }
        val createdService = Service().apply {
            metadata = ObjectMeta().apply {
                name = "wireguard-testuser-service"
                namespace = "microservices"
            }
            spec = io.fabric8.kubernetes.api.model.ServiceSpec().apply {
                ports = listOf(io.fabric8.kubernetes.api.model.ServicePort().apply {
                    nodePort = 31820
                })
            }
        }

        every { kubernetesClient.namespace } returns "microservices"
        every { kubernetesClient.apps().deployments() } returns deploymentsOperation
        every { deploymentsOperation.inNamespace("microservices") } returns deploymentNamespaceOperation
        every { deploymentNamespaceOperation.load(any<java.io.InputStream>()) } returns deploymentResource
        every { deploymentResource.create() } returns createdDeployment

        every { kubernetesClient.services() } returns servicesOperation
        every { servicesOperation.inNamespace("microservices") } returns serviceNamespaceOperation
        every { serviceNamespaceOperation.load(any<java.io.InputStream>()) } returns serviceResource
        every { serviceResource.create() } returns createdService

        val result = service.createDeployment("testuser")

        assertEquals("wireguard-testuser", result.name)
        assertEquals("microservices", result.namespace)
        assertEquals(1, result.replicas)
        assertEquals(1, result.readyReplicas)
        assertEquals(listOf("wireguard"), result.containers)
    }

    @Test
    fun `getPeerConfig returns config file content successfully`() {
        val podsOperation = mockk<MixedOperation<Pod, PodList, PodResource>>()
        val podNamespaceOperation = mockk<NonNamespaceOperation<Pod, PodList, PodResource>>()
        val podResource = mockk<PodResource>()
        val copyOrReadable = mockk<CopyOrReadable>()

        val testPod = Pod().apply {
            metadata = ObjectMeta().apply {
                name = "wireguard-testuser-12345"
                namespace = "microservices"
            }
        }
        val podList = PodList().apply {
            items = listOf(testPod)
        }

        every { kubernetesClient.namespace } returns "microservices"
        every { kubernetesClient.pods() } returns podsOperation
        every { podsOperation.inNamespace("microservices") } returns podNamespaceOperation
        every { podNamespaceOperation.withLabel("app", "wireguard") } returns podNamespaceOperation
        every { podNamespaceOperation.withLabel("user", "testuser") } returns podNamespaceOperation
        every { podNamespaceOperation.list() } returns podList

        every { podNamespaceOperation.withName("wireguard-testuser-12345") } returns podResource
        every { podResource.file("/config/peer_testuser/peer_testuser.conf") } returns copyOrReadable
        every { copyOrReadable.read() } returns "[Interface]\nPrivateKey = xxx".byteInputStream()

        val configContent = service.getPeerConfig("testuser")

        assertEquals("[Interface]\nPrivateKey = xxx", configContent)
    }

    @Test
    fun `getPeerConfig throws 404 when pod is not found`() {
        val podsOperation = mockk<MixedOperation<Pod, PodList, PodResource>>()
        val podNamespaceOperation = mockk<NonNamespaceOperation<Pod, PodList, PodResource>>()

        val emptyPodList = PodList().apply { items = emptyList() }

        every { kubernetesClient.namespace } returns "microservices"
        every { kubernetesClient.pods() } returns podsOperation
        every { podsOperation.inNamespace("microservices") } returns podNamespaceOperation
        every { podNamespaceOperation.withLabel("app", "wireguard") } returns podNamespaceOperation
        every { podNamespaceOperation.withLabel("user", "testuser") } returns podNamespaceOperation
        every { podNamespaceOperation.list() } returns emptyPodList

        assertThrows<ResponseStatusException> {
            service.getPeerConfig("testuser")
        }
    }
}
