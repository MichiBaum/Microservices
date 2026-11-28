package com.michibaum.gateway_discovery.publisher

import com.michibaum.gateway_discovery.model.GatewayMetadataCodec
import com.michibaum.gateway_discovery.sitemap.SitemapUrlProvider
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean
import org.springframework.core.env.Environment

class EurekaGatewayMetadataBeanPostProcessor(
    private val environment: Environment,
    private val beanFactory: ListableBeanFactory,
    private val propertiesProvider: ObjectProvider<GatewayPublishProperties>
) : BeanPostProcessor {

    private val log = LogFactory.getLog(EurekaGatewayMetadataBeanPostProcessor::class.java)

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean !is EurekaInstanceConfigBean) {
            return bean
        }

        val properties = propertiesProvider.getIfAvailable { GatewayPublishProperties() }
        if (!properties.enabled) {
            log.debug("Gateway metadata publishing is disabled via gateway.publish.enabled=false")
            return bean
        }

        val resolvedServiceId = resolveServiceId(properties, bean)
        if (resolvedServiceId == null) {
            log.warn("Skipping gateway metadata publication because neither gateway.publish.service-id nor spring.application.name is set")
            return bean
        }

        val hasSitemapUrlProvider = beanFactory
            .getBeanNamesForType(SitemapUrlProvider::class.java, false, false)
            .isNotEmpty()

        val metadata = properties.toMetadata(
            resolvedServiceId = resolvedServiceId,
            hasSitemapUrlProvider = hasSitemapUrlProvider
        )
        val encodedEntries = GatewayMetadataCodec.encodeToMap(metadata)
        bean.metadataMap.putAll(encodedEntries)

        log.info(
            "Published gateway discovery metadata for service '$resolvedServiceId' " +
                "(routes=${metadata.routes.size}, hasDynamicSitemap=${metadata.sitemap.endpointPath != null})"
        )
        return bean
    }

    private fun resolveServiceId(
        properties: GatewayPublishProperties,
        bean: EurekaInstanceConfigBean
    ): String? {
        properties.serviceId?.trim()?.takeIf { it.isNotEmpty() }?.let { return it }
        environment.getProperty("spring.application.name")?.trim()?.takeIf { it.isNotEmpty() }?.let { return it }
        return bean.appname?.trim()?.takeIf { it.isNotEmpty() && !it.equals("unknown", ignoreCase = true) }
    }
}
