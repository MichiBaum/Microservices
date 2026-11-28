package com.michibaum.gatewayservice.config

import com.michibaum.gateway_discovery.model.GatewayMetadataCodec
import com.michibaum.gateway_discovery.model.GatewayMetadataConstants
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import com.michibaum.gateway_discovery.model.RouteDefinitionMetadata
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import java.util.concurrent.atomic.AtomicInteger

class ServiceGatewayConfigStoreUT {

    @Test
    fun `parses DiscoveryClient metadata and skips listener notification when hashes are unchanged`() {
        val discoveryClient = mockk<DiscoveryClient>()
        val chessMetadata = GatewayServiceMetadata(
            serviceId = "chess-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("chess.michibaum.*"), order = 0))
        )
        val instance = DefaultServiceInstance(
            "chess-1",
            "chess-service",
            "10.0.0.1",
            8080,
            false,
            GatewayMetadataCodec.encodeToMap(chessMetadata)
        )

        every { discoveryClient.services } returns listOf("chess-service")
        every { discoveryClient.getInstances("chess-service") } returns listOf(instance)

        val store = ServiceGatewayConfigStore(discoveryClient)
        val notifications = AtomicInteger(0)
        store.registerListener { notifications.incrementAndGet() }

        val firstRefresh = store.refresh()
        assertThat(firstRefresh.updated).isTrue()
        assertThat(firstRefresh.activeServices).containsExactly("chess-service")
        assertThat(notifications.get()).isEqualTo(1)
        assertThat(store.currentServices()).containsExactly(chessMetadata)

        val secondRefresh = store.refresh()
        assertThat(secondRefresh.updated).isFalse()
        assertThat(notifications.get()).isEqualTo(1)
    }

    @Test
    fun `selects newest replica metadata during rolling deployment`() {
        val discoveryClient = mockk<DiscoveryClient>()
        val oldMetadata = GatewayServiceMetadata(
            serviceId = "chess-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("old-chess.michibaum.*")))
        )
        val newMetadata = GatewayServiceMetadata(
            serviceId = "chess-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("chess.michibaum.*")))
        )

        val olderInstance = DefaultServiceInstance(
            "chess-old",
            "chess-service",
            "10.0.0.1",
            8080,
            false,
            GatewayMetadataCodec.encodeToMap(oldMetadata) + ("lastUpdatedTimestamp" to "1000")
        )
        val newerInstance = DefaultServiceInstance(
            "chess-new",
            "chess-service",
            "10.0.0.2",
            8080,
            false,
            GatewayMetadataCodec.encodeToMap(newMetadata) + ("lastUpdatedTimestamp" to "2000")
        )

        every { discoveryClient.services } returns listOf("chess-service")
        every { discoveryClient.getInstances("chess-service") } returns listOf(olderInstance, newerInstance)

        val store = ServiceGatewayConfigStore(discoveryClient)
        store.refresh()

        assertThat(store.currentServices()).containsExactly(newMetadata)
    }

    @Test
    fun `ignores malformed json and unsupported schema versions while preserving healthy services`() {
        val discoveryClient = mockk<DiscoveryClient>()
        val healthyMetadata = GatewayServiceMetadata(
            serviceId = "website-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("michibaum.*"), order = 1000))
        )
        val healthyInstance = DefaultServiceInstance(
            "web-1",
            "website-service",
            "10.0.0.10",
            8080,
            false,
            GatewayMetadataCodec.encodeToMap(healthyMetadata)
        )
        val malformedInstance = DefaultServiceInstance(
            "bad-1",
            "broken-service",
            "10.0.0.11",
            8080,
            false,
            mapOf(GatewayMetadataConstants.KEY_CONFIG to "{invalid-json")
        )
        val unsupportedVersionInstance = DefaultServiceInstance(
            "bad-2",
            "future-service",
            "10.0.0.12",
            8080,
            false,
            mapOf(
                GatewayMetadataConstants.KEY_SCHEMA_VERSION to "99",
                GatewayMetadataConstants.KEY_CONFIG to """{"schemaVersion": 99, "serviceId": "future-service"}"""
            )
        )

        every { discoveryClient.services } returns listOf("website-service", "broken-service", "future-service")
        every { discoveryClient.getInstances("website-service") } returns listOf(healthyInstance)
        every { discoveryClient.getInstances("broken-service") } returns listOf(malformedInstance)
        every { discoveryClient.getInstances("future-service") } returns listOf(unsupportedVersionInstance)

        val store = ServiceGatewayConfigStore(discoveryClient)
        val summary = store.refresh()

        assertThat(summary.activeServices).containsExactly("website-service")
        assertThat(store.currentServices()).containsExactly(healthyMetadata)
    }

    @Test
    fun `retains last-known-good snapshot when service goes offline until explicitly evicted`() {
        val discoveryClient = mockk<DiscoveryClient>()
        val chessMetadata = GatewayServiceMetadata(
            serviceId = "chess-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("chess.michibaum.*")))
        )
        val instance = DefaultServiceInstance(
            "chess-1",
            "chess-service",
            "10.0.0.1",
            8080,
            false,
            GatewayMetadataCodec.encodeToMap(chessMetadata)
        )

        every { discoveryClient.services } returns listOf("chess-service")
        every { discoveryClient.getInstances("chess-service") } returns listOf(instance)

        val store = ServiceGatewayConfigStore(discoveryClient)
        store.refresh()
        assertThat(store.currentSnapshot().entriesByServiceId["chess-service"]?.liveInstanceCount).isEqualTo(1)

        // Simulate outage: 0 live instances
        every { discoveryClient.services } returns emptyList()
        val outageSummary = store.refresh(evictMissingServices = false)

        assertThat(outageSummary.updated).isFalse()
        assertThat(outageSummary.activeServices).containsExactly("chess-service")
        assertThat(store.currentSnapshot().entriesByServiceId["chess-service"]?.liveInstanceCount).isEqualTo(0)

        // Operator explicitly purges stale services
        val evictionSummary = store.refresh(evictMissingServices = true)
        assertThat(evictionSummary.updated).isTrue()
        assertThat(evictionSummary.evictedServices).containsExactly("chess-service")
        assertThat(store.currentServices()).isEmpty()
    }
}
