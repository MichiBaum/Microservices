package com.michibaum.gateway_discovery.sitemap

import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@AutoConfiguration
@ConditionalOnClass(Endpoint::class)
@ConditionalOnBean(SitemapUrlProvider::class)
class GatewaySitemapAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun sitemapActuatorEndpoint(providers: List<SitemapUrlProvider>): SitemapActuatorEndpoint {
        return SitemapActuatorEndpoint(providers)
    }
}
