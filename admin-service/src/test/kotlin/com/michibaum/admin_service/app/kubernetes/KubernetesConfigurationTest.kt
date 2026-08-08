package com.michibaum.admin_service.app.kubernetes

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class KubernetesConfigurationTest {

    @Test
    fun `kubernetesClient creates a valid KubernetesClient instance`() {
        val configuration = KubernetesConfiguration()
        val client = configuration.kubernetesClient()
        assertNotNull(client)
    }
}
