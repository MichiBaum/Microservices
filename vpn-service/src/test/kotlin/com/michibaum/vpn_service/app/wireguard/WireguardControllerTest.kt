package com.michibaum.vpn_service.app.wireguard

import com.michibaum.vpn_service.app.kubernetes.dto.DeploymentDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class WireguardControllerTest {

    private val wireguardService = mockk<WireguardService>()
    private val controller = WireguardController(wireguardService)
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `createDeployment with username param returns created DeploymentDto`() {
        val deploymentDto = DeploymentDto(
            name = "wireguard-john-doe",
            namespace = "microservices",
            replicas = 1,
            readyReplicas = 1,
            creationTimestamp = "2026-08-29T10:00:00Z",
            containers = listOf("wireguard")
        )

        every { wireguardService.createDeployment("john-doe", null) } returns deploymentDto

        mockMvc.perform(post("/api/wireguard").param("username", "john-doe"))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("wireguard-john-doe"))
            .andExpect(jsonPath("$.namespace").value("microservices"))

        verify(exactly = 1) { wireguardService.createDeployment("john-doe", null) }
    }

    @Test
    fun `createDeployment throws bad request when username is not set`() {
        mockMvc.perform(post("/api/wireguard"))
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { wireguardService.createDeployment(any(), any()) }
    }

    @Test
    fun `createDeployment throws bad request when username is anonymous`() {
        mockMvc.perform(post("/api/wireguard").param("username", "anonymous"))
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { wireguardService.createDeployment(any(), any()) }
    }

    @Test
    fun `getPeerConfig with username param returns config string`() {
        val configContent = "[Interface]\nPrivateKey = xxx"
        every { wireguardService.getPeerConfig("john-doe", null) } returns configContent

        mockMvc.perform(get("/api/wireguard/config").param("username", "john-doe"))
            .andExpect(status().isOk)
            .andExpect(content().string(configContent))

        verify(exactly = 1) { wireguardService.getPeerConfig("john-doe", null) }
    }

    @Test
    fun `getPeerConfig throws bad request when username is not set`() {
        mockMvc.perform(get("/api/wireguard/config"))
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { wireguardService.getPeerConfig(any(), any()) }
    }
}
