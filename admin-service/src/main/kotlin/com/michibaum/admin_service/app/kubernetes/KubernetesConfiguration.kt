package com.michibaum.admin_service.app.kubernetes

import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KubernetesConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun kubernetesClient(): KubernetesClient =
        KubernetesClientBuilder().build()

}
