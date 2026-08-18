package com.michibaum.admin_service.app.kubernetes

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

class KubernetesUiExtensionTest {

    @Test
    fun `routes file exists and contains k8s route`() {
        val resource = ClassPathResource("META-INF/spring-boot-admin-server-ui/extensions/k8s/routes.txt")
        assertTrue(resource.exists(), "routes.txt resource should exist on classpath")

        val content = resource.inputStream.bufferedReader().use { it.readText() }
        assertTrue(content.contains("/k8s"), "routes.txt should contain /k8s route")
    }

    @Test
    fun `extension javascript file exists and registers SBA view`() {
        val resource = ClassPathResource("META-INF/spring-boot-admin-server-ui/extensions/k8s/k8s.js")
        assertTrue(resource.exists(), "k8s.js resource should exist on classpath")

        val content = resource.inputStream.bufferedReader().use { it.readText() }
        assertTrue(content.contains("SBA.use"), "k8s.js should register SBA extension via SBA.use")
        assertTrue(content.contains("/api/k8s/pods"), "k8s.js should fetch pods endpoint")
        assertTrue(content.contains("/api/k8s/services"), "k8s.js should fetch services endpoint")
    }
}
