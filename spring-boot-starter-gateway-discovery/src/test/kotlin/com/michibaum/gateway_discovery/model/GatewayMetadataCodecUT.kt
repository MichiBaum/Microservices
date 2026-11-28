package com.michibaum.gateway_discovery.model

import com.michibaum.permission_library.Permissions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class GatewayMetadataCodecUT {

    @Test
    fun `round-trip serialization preserves full metadata and produces deterministic hash`() {
        val metadata = GatewayServiceMetadata(
            serviceId = "chess-service",
            routes = listOf(
                RouteDefinitionMetadata(
                    id = "chess-main",
                    hosts = listOf("chess.michibaum.*"),
                    paths = listOf("/api/**"),
                    order = 0,
                    requiredPermissions = listOf(Permissions.CHESS_SERVICE, Permissions.ADMIN_SERVICE),
                    circuitBreaker = CircuitBreakerMetadata(
                        timeoutSeconds = 15,
                        slidingWindowSize = 100
                    )
                )
            ),
            cors = CorsMetadata(
                allowedOriginPatterns = listOf("https://partner.example.com"),
                allowedMethods = listOf("GET", "POST"),
                allowedHeaders = listOf("Authorization", "Content-Type"),
                allowCredentials = true
            ),
            sitemap = SitemapMetadata(
                staticLocations = listOf("/chess", "/chess/events"),
                endpointPath = "/actuator/sitemap"
            ),
            robots = RobotsMetadata(
                crawlers = listOf(
                    CrawlerRuleMetadata(
                        userAgent = "*",
                        allowed = listOf("/chess"),
                        disallowed = listOf("/chess/internal")
                    )
                )
            )
        )

        val encodedMap = GatewayMetadataCodec.encodeToMap(metadata)
        assertThat(encodedMap).containsKeys(
            GatewayMetadataConstants.KEY_SCHEMA_VERSION,
            GatewayMetadataConstants.KEY_HASH,
            GatewayMetadataConstants.KEY_CONFIG
        )
        assertThat(encodedMap[GatewayMetadataConstants.KEY_SCHEMA_VERSION]).isEqualTo("1")
        assertThat(encodedMap[GatewayMetadataConstants.KEY_HASH]).hasSize(64)
        assertThat(encodedMap[GatewayMetadataConstants.KEY_HASH])
            .isEqualTo(GatewayMetadataCodec.computeHash(metadata))

        val decoded = GatewayMetadataCodec.decodeFromMap(encodedMap)
        assertThat(decoded).isNotNull
        assertThat(decoded?.hash).isEqualTo(encodedMap[GatewayMetadataConstants.KEY_HASH])
        assertThat(decoded?.metadata).isEqualTo(metadata)
    }

    @Test
    fun `deserialization ignores unknown properties for forward compatibility`() {
        val json = """
            {
              "schemaVersion": 1,
              "serviceId": "chess-service",
              "futureTopLevelField": "ignored",
              "routes": [
                {
                  "hosts": ["chess.michibaum.*"],
                  "futureRouteField": 42
                }
              ]
            }
        """.trimIndent()

        val deserialized = GatewayMetadataCodec.deserialize(json)
        assertThat(deserialized.serviceId).isEqualTo("chess-service")
        assertThat(deserialized.routes).hasSize(1)
        assertThat(deserialized.routes.first().hosts).containsExactly("chess.michibaum.*")
    }

    @Test
    fun `decodeFromMap returns null when no gateway config key is present`() {
        assertThat(GatewayMetadataCodec.decodeFromMap(null)).isNull()
        assertThat(GatewayMetadataCodec.decodeFromMap(emptyMap())).isNull()
        assertThat(GatewayMetadataCodec.decodeFromMap(mapOf("prometheus.path" to "/actuator/prometheus"))).isNull()
    }

    @Test
    fun `deserialize and decodeFromMap reject malformed json and unsupported schema versions`() {
        assertThatThrownBy {
            GatewayMetadataCodec.deserialize("{not-valid-json")
        }.isInstanceOf(GatewayMetadataException::class.java)

        assertThatThrownBy {
            GatewayMetadataCodec.deserialize("""{"schemaVersion": 2, "serviceId": "chess-service"}""")
        }.isInstanceOf(GatewayMetadataException::class.java)
            .hasMessageContaining("schemaVersion=2")

        assertThatThrownBy {
            GatewayMetadataCodec.decodeFromMap(
                mapOf(
                    GatewayMetadataConstants.KEY_SCHEMA_VERSION to "99",
                    GatewayMetadataConstants.KEY_CONFIG to """{"schemaVersion": 1, "serviceId": "chess-service"}"""
                )
            )
        }.isInstanceOf(GatewayMetadataException::class.java)
            .hasMessageContaining("schemaVersion=99")
    }
}
