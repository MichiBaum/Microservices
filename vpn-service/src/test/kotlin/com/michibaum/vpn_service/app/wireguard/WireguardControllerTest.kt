package com.michibaum.vpn_service.app.wireguard

import com.michibaum.permission_library.Permissions
import com.michibaum.vpn_service.app.kubernetes.dto.DeploymentDto
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
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

    private val validAuth = UsernamePasswordAuthenticationToken(
        "john-doe",
        null,
        listOf(SimpleGrantedAuthority(Permissions.VPN_SERVICE_OWN_USER.name))
    )

    private val anonymousAuth = UsernamePasswordAuthenticationToken(
        "anonymous",
        null,
        listOf(SimpleGrantedAuthority(Permissions.VPN_SERVICE_OWN_USER.name))
    )

    private val noPermissionAuth = UsernamePasswordAuthenticationToken(
        "john-doe",
        null,
        listOf(SimpleGrantedAuthority(Permissions.CHESS_SERVICE.name))
    )

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `createDeployment with valid authentication returns created DeploymentDto`() {
        val deploymentDto = DeploymentDto(
            name = "wireguard-john-doe",
            namespace = "microservices",
            replicas = 1,
            readyReplicas = 1,
            creationTimestamp = "2026-08-29T10:00:00Z",
            containers = listOf("wireguard")
        )

        every { wireguardService.createDeployment("john-doe", null) } returns deploymentDto

        mockMvc.perform(post("/api/wireguard").principal(validAuth))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("wireguard-john-doe"))
            .andExpect(jsonPath("$.namespace").value("microservices"))

        verify(exactly = 1) { wireguardService.createDeployment("john-doe", null) }
    }

    @Test
    fun `createDeployment throws bad request when user is not authenticated`() {
        mockMvc.perform(post("/api/wireguard"))
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { wireguardService.createDeployment(any(), any()) }
    }

    @Test
    fun `createDeployment throws bad request when username is anonymous`() {
        mockMvc.perform(post("/api/wireguard").principal(anonymousAuth))
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { wireguardService.createDeployment(any(), any()) }
    }

    @Test
    fun `createDeployment throws forbidden when user lacks permission`() {
        mockMvc.perform(post("/api/wireguard").principal(noPermissionAuth))
            .andExpect(status().isForbidden)

        verify(exactly = 0) { wireguardService.createDeployment(any(), any()) }
    }

    @Test
    fun `getPeerConfig with valid authentication returns config string`() {
        val configContent = "[Interface]\nPrivateKey = xxx"
        every { wireguardService.getPeerConfig("john-doe", null) } returns configContent

        mockMvc.perform(get("/api/wireguard/config").principal(validAuth))
            .andExpect(status().isOk)
            .andExpect(content().string(configContent))

        verify(exactly = 1) { wireguardService.getPeerConfig("john-doe", null) }
    }

    @Test
    fun `getPeerConfig throws bad request when user is not authenticated`() {
        mockMvc.perform(get("/api/wireguard/config"))
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { wireguardService.getPeerConfig(any(), any()) }
    }

    @Test
    fun `deleteDeployment with valid authentication deletes deployment and returns 204`() {
        every { wireguardService.deleteDeployment("john-doe", null) } just runs

        mockMvc.perform(delete("/api/wireguard").principal(validAuth))
            .andExpect(status().isNoContent)

        verify(exactly = 1) { wireguardService.deleteDeployment("john-doe", null) }
    }

    @Test
    fun `deleteDeployment throws bad request when user is not authenticated`() {
        mockMvc.perform(delete("/api/wireguard"))
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { wireguardService.deleteDeployment(any(), any()) }
    }

    @Test
    fun `deleteDeployment throws forbidden when user lacks permission`() {
        mockMvc.perform(delete("/api/wireguard").principal(noPermissionAuth))
            .andExpect(status().isForbidden)

        verify(exactly = 0) { wireguardService.deleteDeployment(any(), any()) }
    }
}
