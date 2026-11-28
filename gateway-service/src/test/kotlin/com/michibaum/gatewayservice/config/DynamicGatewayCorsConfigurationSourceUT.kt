package com.michibaum.gatewayservice.config

import com.michibaum.gateway_discovery.model.CorsMetadata
import com.michibaum.gateway_discovery.model.GatewayMetadataCodec
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import com.michibaum.gateway_discovery.model.RouteDefinitionMetadata
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.mock.web.MockHttpServletRequest

class DynamicGatewayCorsConfigurationSourceUT {

    @Test
    fun `merges baseline origins with host-matched service origins and isolates other hosts`() {
        val discoveryClient = mockk<DiscoveryClient>()

        val chessMeta = GatewayServiceMetadata(
            serviceId = "chess-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("chess.michibaum.*"), order = 0)),
            cors = CorsMetadata(
                allowedOriginPatterns = listOf("https://partner-chess.example.com"),
                allowedMethods = listOf("GET", "POST", "OPTIONS")
            )
        )
        val vpnMeta = GatewayServiceMetadata(
            serviceId = "vpn-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("vpn.michibaum.*"), order = 0))
        )
        val websiteMeta = GatewayServiceMetadata(
            serviceId = "website-service",
            routes = listOf(RouteDefinitionMetadata(hosts = listOf("michibaum.*"), order = 1000)),
            cors = CorsMetadata(
                allowedOriginPatterns = listOf("https://website-preview.example.com")
            )
        )

        every { discoveryClient.services } returns listOf("chess-service", "vpn-service", "website-service")
        every { discoveryClient.getInstances("chess-service") } returns listOf(
            DefaultServiceInstance("c1", "chess-service", "10.0.0.1", 8080, false, GatewayMetadataCodec.encodeToMap(chessMeta))
        )
        every { discoveryClient.getInstances("vpn-service") } returns listOf(
            DefaultServiceInstance("v1", "vpn-service", "10.0.0.2", 8080, false, GatewayMetadataCodec.encodeToMap(vpnMeta))
        )
        every { discoveryClient.getInstances("website-service") } returns listOf(
            DefaultServiceInstance("w1", "website-service", "10.0.0.3", 8080, false, GatewayMetadataCodec.encodeToMap(websiteMeta))
        )

        val store = ServiceGatewayConfigStore(discoveryClient)
        store.refresh()

        val corsProperties = CorsProperties(
            baseAllowedOriginPatterns = listOf("https://michibaum.ch", "https://*.michibaum.ch")
        )
        val corsSource = DynamicGatewayCorsConfigurationSource(corsProperties, store)

        // chess.michibaum.ch allows both baseline and chess-specific origins
        val chessRequest = MockHttpServletRequest("OPTIONS", "/api/events").apply {
            addHeader("Host", "chess.michibaum.ch")
        }
        val chessCors = corsSource.getCorsConfiguration(chessRequest)
        assertThat(chessCors.checkOrigin("https://michibaum.ch")).isEqualTo("https://michibaum.ch")
        assertThat(chessCors.checkOrigin("https://partner-chess.example.com")).isEqualTo("https://partner-chess.example.com")
        assertThat(chessCors.checkOrigin("https://website-preview.example.com")).isNull()
        assertThat(chessCors.allowedMethods).containsExactly("GET", "POST", "OPTIONS")

        // vpn.michibaum.ch allows baseline origins ONLY and rejects chess-service's extra origin
        val vpnRequest = MockHttpServletRequest("OPTIONS", "/api/peers").apply {
            addHeader("Host", "vpn.michibaum.ch:443")
        }
        val vpnCors = corsSource.getCorsConfiguration(vpnRequest)
        assertThat(vpnCors.checkOrigin("https://vpn.michibaum.ch")).isEqualTo("https://vpn.michibaum.ch")
        assertThat(vpnCors.checkOrigin("https://partner-chess.example.com")).isNull()

        // michibaum.ch matches website-service catch-all
        val webRequest = MockHttpServletRequest("OPTIONS", "/").apply {
            addHeader("Host", "michibaum.ch")
        }
        val webCors = corsSource.getCorsConfiguration(webRequest)
        assertThat(webCors.checkOrigin("https://website-preview.example.com")).isEqualTo("https://website-preview.example.com")
    }
}
