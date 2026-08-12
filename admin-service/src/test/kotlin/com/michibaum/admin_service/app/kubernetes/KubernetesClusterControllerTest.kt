package com.michibaum.admin_service.app.kubernetes

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.admin_service.app.kubernetes.dto.PodDto
import com.michibaum.admin_service.app.kubernetes.dto.ServiceDto
import com.michibaum.admin_service.app.kubernetes.dto.ServicePortDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class KubernetesClusterControllerTest {

    private val kubernetesClusterService = mockk<KubernetesClusterService>()
    private val controller = KubernetesClusterController(kubernetesClusterService)
    private val objectMapper = ObjectMapper()
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `getPods returns list of PodDto`() {
        val podDto = PodDto(
            name = "test-pod",
            namespace = "microservices",
            status = "Running",
            podIp = "10.244.0.5",
            nodeName = "node-1",
            creationTimestamp = "2026-08-05T09:00:00Z",
            containers = listOf("app")
        )

        every { kubernetesClusterService.getPods(null) } returns listOf(podDto)

        mockMvc.perform(get("/api/k8s/pods"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("test-pod"))
            .andExpect(jsonPath("$[0].namespace").value("microservices"))
            .andExpect(jsonPath("$[0].status").value("Running"))
            .andExpect(jsonPath("$[0].podIp").value("10.244.0.5"))

        verify(exactly = 1) { kubernetesClusterService.getPods(null) }
    }

    @Test
    fun `getServices returns list of ServiceDto`() {
        val serviceDto = ServiceDto(
            name = "test-service",
            namespace = "microservices",
            type = "ClusterIP",
            clusterIp = "10.96.0.10",
            ports = listOf(ServicePortDto(name = "http", port = 80, targetPort = "80", protocol = "TCP")),
            selector = mapOf("app" to "test-service")
        )

        every { kubernetesClusterService.getServices(null) } returns listOf(serviceDto)

        mockMvc.perform(get("/api/k8s/services"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("test-service"))
            .andExpect(jsonPath("$[0].type").value("ClusterIP"))
            .andExpect(jsonPath("$[0].ports[0].port").value(80))

        verify(exactly = 1) { kubernetesClusterService.getServices(null) }
    }

}
