package com.michibaum.admin_service.app.kubernetes

import com.michibaum.admin_service.app.kubernetes.dto.PodDto
import com.michibaum.admin_service.app.kubernetes.dto.ServiceDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/k8s")
class KubernetesClusterController(
    private val kubernetesClusterService: KubernetesClusterService
) {

    @GetMapping("/pods")
    fun getPods(
        @RequestParam(required = false) namespace: String?
    ): List<PodDto> =
        kubernetesClusterService.getPods(namespace)

    @GetMapping("/services")
    fun getServices(
        @RequestParam(required = false) namespace: String?
    ): List<ServiceDto> =
        kubernetesClusterService.getServices(namespace)

}
