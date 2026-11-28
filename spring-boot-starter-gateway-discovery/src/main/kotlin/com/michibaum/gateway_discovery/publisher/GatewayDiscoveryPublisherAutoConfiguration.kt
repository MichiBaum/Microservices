package com.michibaum.gateway_discovery.publisher

import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment

@AutoConfiguration
@ConditionalOnClass(EurekaInstanceConfigBean::class)
@ConditionalOnProperty(prefix = "gateway.publish", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(GatewayPublishProperties::class)
class GatewayDiscoveryPublisherAutoConfiguration {

    companion object {
        @Bean
        @JvmStatic
        @ConditionalOnMissingBean
        fun eurekaGatewayMetadataBeanPostProcessor(
            environment: Environment,
            beanFactory: ListableBeanFactory,
            propertiesProvider: ObjectProvider<GatewayPublishProperties>
        ): EurekaGatewayMetadataBeanPostProcessor {
            return EurekaGatewayMetadataBeanPostProcessor(
                environment = environment,
                beanFactory = beanFactory,
                propertiesProvider = propertiesProvider
            )
        }
    }
}
