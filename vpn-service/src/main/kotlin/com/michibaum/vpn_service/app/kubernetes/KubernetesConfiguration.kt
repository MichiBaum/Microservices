package com.michibaum.vpn_service.app.kubernetes

import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [KubernetesProperties::class])
class KubernetesConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun kubernetesClient(): KubernetesClient =
        KubernetesClientBuilder().build()

}
