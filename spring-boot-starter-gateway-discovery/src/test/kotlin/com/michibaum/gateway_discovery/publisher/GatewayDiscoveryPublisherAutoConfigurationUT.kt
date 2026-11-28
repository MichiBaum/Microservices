package com.michibaum.gateway_discovery.publisher

import com.michibaum.gateway_discovery.model.GatewayMetadataCodec
import com.michibaum.gateway_discovery.model.GatewayMetadataConstants
import com.michibaum.gateway_discovery.sitemap.GatewaySitemapAutoConfiguration
import com.michibaum.gateway_discovery.sitemap.SitemapActuatorEndpoint
import com.michibaum.gateway_discovery.sitemap.SitemapUrlProvider
import com.michibaum.permission_library.Permissions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.cloud.commons.util.InetUtils
import org.springframework.cloud.commons.util.InetUtilsProperties
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class GatewayDiscoveryPublisherAutoConfigurationUT {

    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(
            AutoConfigurations.of(
                GatewayDiscoveryPublisherAutoConfiguration::class.java,
                GatewaySitemapAutoConfiguration::class.java
            )
        )
        .withUserConfiguration(EurekaTestConfig::class.java)

    @Test
    fun `populates EurekaInstanceConfigBean metadataMap from gateway publish properties`() {
        contextRunner
            .withPropertyValues(
                "spring.application.name=admin-service",
                "gateway.publish.routes[0].hosts[0]=admin.michibaum.*",
                "gateway.publish.routes[0].order=0",
                "gateway.publish.routes[0].required-permissions[0]=ADMIN_SERVICE",
                "gateway.publish.cors.allowed-origin-patterns[0]=https://admin-preview.example.com",
                "gateway.publish.sitemap.static-locations[0]=/admin",
                "gateway.publish.robots.crawlers[0].user-agent=*",
                "gateway.publish.robots.crawlers[0].disallowed[0]=/admin/private"
            )
            .run { context ->
                assertThat(context).hasNotFailed()
                assertThat(context).doesNotHaveBean(SitemapActuatorEndpoint::class.java)

                val instanceConfig = context.getBean(EurekaInstanceConfigBean::class.java)
                val metadataMap = instanceConfig.metadataMap

                assertThat(metadataMap[GatewayMetadataConstants.KEY_SCHEMA_VERSION]).isEqualTo("1")
                assertThat(metadataMap[GatewayMetadataConstants.KEY_HASH]).hasSize(64)

                val decoded = GatewayMetadataCodec.decodeFromMap(metadataMap)
                assertThat(decoded).isNotNull
                val metadata = requireNotNull(decoded).metadata
                assertThat(metadata.serviceId).isEqualTo("admin-service")
                assertThat(metadata.routes).hasSize(1)
                assertThat(metadata.routes.first().hosts).containsExactly("admin.michibaum.*")
                assertThat(metadata.routes.first().requiredPermissions).containsExactly(Permissions.ADMIN_SERVICE)
                assertThat(metadata.cors.allowedOriginPatterns).containsExactly("https://admin-preview.example.com")
                assertThat(metadata.sitemap.staticLocations).containsExactly("/admin")
                assertThat(metadata.sitemap.endpointPath).isNull()
                assertThat(metadata.robots.crawlers.first().disallowed).containsExactly("/admin/private")
            }
    }

    @Test
    fun `automatically advertises actuator sitemap endpoint when SitemapUrlProvider bean exists`() {
        contextRunner
            .withUserConfiguration(SitemapProviderTestConfig::class.java)
            .withPropertyValues(
                "spring.application.name=chess-service",
                "gateway.publish.routes[0].hosts[0]=chess.michibaum.*",
                "gateway.publish.sitemap.static-locations[0]=/chess"
            )
            .run { context ->
                assertThat(context).hasNotFailed()
                assertThat(context).hasSingleBean(SitemapActuatorEndpoint::class.java)

                val instanceConfig = context.getBean(EurekaInstanceConfigBean::class.java)
                val decoded = requireNotNull(GatewayMetadataCodec.decodeFromMap(instanceConfig.metadataMap))
                assertThat(decoded.metadata.sitemap.endpointPath)
                    .isEqualTo(GatewayMetadataConstants.DEFAULT_SITEMAP_ENDPOINT_PATH)
                assertThat(decoded.metadata.sitemap.staticLocations).containsExactly("/chess")
            }
    }

    @Test
    fun `does not publish metadata when gateway publish enabled is false`() {
        contextRunner
            .withPropertyValues(
                "spring.application.name=gateway-service",
                "gateway.publish.enabled=false",
                "gateway.publish.routes[0].hosts[0]=ignored.michibaum.*"
            )
            .run { context ->
                assertThat(context).hasNotFailed()
                assertThat(context).doesNotHaveBean(EurekaGatewayMetadataBeanPostProcessor::class.java)

                val instanceConfig = context.getBean(EurekaInstanceConfigBean::class.java)
                assertThat(instanceConfig.metadataMap).doesNotContainKey(GatewayMetadataConstants.KEY_CONFIG)
            }
    }

    @Test
    fun `fails startup when a published route declares neither hosts nor paths`() {
        contextRunner
            .withPropertyValues(
                "spring.application.name=broken-service",
                "gateway.publish.routes[0].order=0"
            )
            .run { context ->
                assertThat(context).hasFailed()
                assertThat(context.startupFailure)
                    .rootCause()
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("must declare at least one host or path pattern")
            }
    }

    @Configuration
    class EurekaTestConfig {
        @Bean(destroyMethod = "close")
        fun inetUtils(): InetUtils = InetUtils(InetUtilsProperties())

        @Bean
        fun eurekaInstanceConfigBean(inetUtils: InetUtils): EurekaInstanceConfigBean {
            return EurekaInstanceConfigBean(inetUtils)
        }
    }

    @Configuration
    class SitemapProviderTestConfig {
        @Bean
        fun chessSitemapUrlProvider(): SitemapUrlProvider = SitemapUrlProvider {
            listOf("/chess/events/1")
        }
    }
}
