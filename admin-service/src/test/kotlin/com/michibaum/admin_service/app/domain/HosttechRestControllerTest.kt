package com.michibaum.admin_service.app.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class HosttechRestControllerTest {

    private val hosttechDomainService = mockk<HosttechDomainService>()
    private val controller = HosttechRestController(hosttechDomainService)
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `allZones calls service with extracted token`() {
        val token = "my-secret-token"
        every { hosttechDomainService.getAllZones(token) } returns emptyList()

        mockMvc.perform(
            get("/api/hosttech/zones")
                .param("Hosttech-Token", token)
        ).andExpect(status().isOk)

        verify(exactly = 1) { hosttechDomainService.getAllZones(token) }
    }

    @Test
    fun `removeAllAcmeChallengeRecords calls service with extracted token`() {
        val token = "my-secret-token"
        every { hosttechDomainService.removeAllAcmeChallengeRecords(token) } returns Unit

        mockMvc.perform(
            post("/api/hosttech/remove-acme-challenge-records")
                .param("Hosttech-Token", token)
        ).andExpect(status().isOk)

        verify(exactly = 1) { hosttechDomainService.removeAllAcmeChallengeRecords(token) }
    }
}
