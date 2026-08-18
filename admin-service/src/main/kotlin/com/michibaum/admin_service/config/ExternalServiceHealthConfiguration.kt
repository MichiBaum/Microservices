package com.michibaum.admin_service.config

import com.michibaum.admin_service.app.kubernetes.KubernetesClusterService
import de.codecentric.boot.admin.server.domain.entities.Instance
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import de.codecentric.boot.admin.server.domain.values.StatusInfo
import de.codecentric.boot.admin.server.services.ApiMediaTypeHandler
import de.codecentric.boot.admin.server.services.HealthGroupsCache
import de.codecentric.boot.admin.server.services.StatusUpdater
import de.codecentric.boot.admin.server.web.client.InstanceWebClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Configuration
class ExternalServiceHealthConfiguration(
    private val kubernetesClusterService: KubernetesClusterService
) {

    @Bean
    @Primary
    fun statusUpdater(
        repository: InstanceRepository,
        instanceWebClientBuilder: InstanceWebClient.Builder,
        healthGroupsCache: HealthGroupsCache
    ): StatusUpdater {
        return object : StatusUpdater(repository, instanceWebClientBuilder.build(), ApiMediaTypeHandler(), healthGroupsCache) {
            override fun doUpdateStatus(instance: Instance): Mono<Instance> {
                val name = instance.registration.name
                val checkType = instance.registration.metadata["sba-check"]

                val isDatabase = name.endsWith("-db")
                val isObservability = name in setOf("jaeger", "jaeger-storage", "prometheus")

                return if (checkType == "kubernetes" || isDatabase || isObservability) {
                    Mono.fromCallable {
                        val health = kubernetesClusterService.getServiceHealth(name)
                        val statusInfo = StatusInfo.valueOf(health.status, health.details)
                        instance.withStatusInfo(statusInfo)
                    }.subscribeOn(Schedulers.boundedElastic())
                } else {
                    super.doUpdateStatus(instance)
                }
            }
        }
    }

}
