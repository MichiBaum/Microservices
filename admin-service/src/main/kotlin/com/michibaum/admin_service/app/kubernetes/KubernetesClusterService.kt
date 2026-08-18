package com.michibaum.admin_service.app.kubernetes

import com.michibaum.admin_service.app.kubernetes.dto.PodDto
import com.michibaum.admin_service.app.kubernetes.dto.ServiceDto
import com.michibaum.admin_service.app.kubernetes.dto.ServicePortDto
import io.fabric8.kubernetes.client.KubernetesClient
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class KubernetesClusterService(
    private val kubernetesClient: KubernetesClient? = null,
    private val kubernetesProperties: KubernetesProperties
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getPods(namespace: String? = null): List<PodDto> {
        val client = kubernetesClient ?: throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE, "Kubernetes client is not available"
        )
        val targetNamespace = resolveNamespace(namespace)
        val podList = try {
            client.pods().inNamespace(targetNamespace).list().items
        } catch (e: Exception) {
            logger.error("Failed to fetch pods from Kubernetes API server in namespace $targetNamespace", e)
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach Kubernetes API Server", e)
        }

        return podList.map { pod ->
            PodDto(
                name = pod.metadata?.name ?: "",
                namespace = pod.metadata?.namespace ?: targetNamespace,
                status = pod.status?.phase ?: "Unknown",
                podIp = pod.status?.podIP,
                nodeName = pod.spec?.nodeName,
                creationTimestamp = pod.metadata?.creationTimestamp,
                containers = pod.spec?.containers?.mapNotNull { it.name } ?: emptyList()
            )
        }
    }

    fun getServices(namespace: String? = null): List<ServiceDto> {
        val client = kubernetesClient ?: throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE, "Kubernetes client is not available"
        )
        val targetNamespace = resolveNamespace(namespace)
        val serviceList = try {
            client.services().inNamespace(targetNamespace).list().items
        } catch (e: Exception) {
            logger.error("Failed to fetch services from Kubernetes API server in namespace $targetNamespace", e)
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to reach Kubernetes API Server", e)
        }

        return serviceList.map { svc ->
            ServiceDto(
                name = svc.metadata?.name ?: "",
                namespace = svc.metadata?.namespace ?: targetNamespace,
                type = svc.spec?.type ?: "ClusterIP",
                clusterIp = svc.spec?.clusterIP,
                ports = svc.spec?.ports?.map { p ->
                    ServicePortDto(
                        name = p.name,
                        port = p.port ?: 0,
                        targetPort = p.targetPort?.getValue()?.toString(),
                        protocol = p.protocol ?: "TCP"
                    )
                } ?: emptyList(),
                selector = svc.spec?.selector ?: emptyMap()
            )
        }
    }

    fun isServiceUp(serviceName: String, namespace: String? = null): Boolean {
        val client = kubernetesClient ?: return false
        val targetNamespace = resolveNamespace(namespace)
        
        return try {
            val service = client.services().inNamespace(targetNamespace).withName(serviceName).get() 
                ?: return false
            
            val selector = service.spec?.selector ?: return false
            if (selector.isEmpty()) return false

            val pods = client.pods().inNamespace(targetNamespace).withLabels(selector).list().items
            pods.any { pod ->
                pod.status?.phase == "Running" && 
                pod.status?.containerStatuses?.all { it.ready } == true
            }
        } catch (e: Exception) {
            logger.error("Failed to check service health for $serviceName in namespace $targetNamespace", e)
            false
        }
    }

    private fun resolveNamespace(namespace: String?): String =
        namespace?.takeIf { it.isNotBlank() }
            ?: kubernetesClient?.namespace?.takeIf { it.isNotBlank() }
            ?: kubernetesProperties.defaultNamespace
}
