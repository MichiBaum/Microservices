package com.michibaum.admin_service.config

import com.michibaum.admin_service.app.kubernetes.KubernetesClusterService
import de.codecentric.boot.admin.server.config.AdminServerProperties
import de.codecentric.boot.admin.server.domain.entities.Instance
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository
import de.codecentric.boot.admin.server.domain.values.InstanceId
import de.codecentric.boot.admin.server.domain.values.StatusInfo
import de.codecentric.boot.admin.server.services.*
import de.codecentric.boot.admin.server.services.endpoints.ChainingStrategy
import de.codecentric.boot.admin.server.services.endpoints.ProbeEndpointsStrategy
import de.codecentric.boot.admin.server.services.endpoints.QueryIndexEndpointStrategy
import de.codecentric.boot.admin.server.web.client.InstanceWebClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Configuration
class ExternalServiceHealthConfiguration(
    private val kubernetesClusterService: KubernetesClusterService,
    private val adminServerProperties: AdminServerProperties
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
                return if (isExternalService(instance)) {
                    Mono.fromCallable {
                        val health = kubernetesClusterService.getServiceHealth(instance.registration.name)
                        val statusInfo = StatusInfo.valueOf(health.status, health.details)
                        instance.withStatusInfo(statusInfo)
                    }.subscribeOn(Schedulers.boundedElastic())
                } else {
                    super.doUpdateStatus(instance)
                }
            }
        }
    }

    @Bean
    @Primary
    fun infoUpdater(
        repository: InstanceRepository,
        instanceWebClientBuilder: InstanceWebClient.Builder,
    ): InfoUpdater {
        return object : InfoUpdater(repository, instanceWebClientBuilder.build(), ApiMediaTypeHandler()) {
            override fun doUpdateInfo(instance: Instance): Mono<Instance> {
                return if (isExternalService(instance)) {
                    Mono.fromCallable {
                        val health = kubernetesClusterService.getServiceHealth(instance.registration.name)
                        val statusInfo = StatusInfo.valueOf(health.status, health.details)
                        instance.withStatusInfo(statusInfo)
                    }.subscribeOn(Schedulers.boundedElastic())
                } else {
                    super.doUpdateInfo(instance)
                }
            }
        }
    }

    @Bean
    @Primary
    fun endpointDetector(
        repository: InstanceRepository,
        instanceWebClientBuilder: InstanceWebClient.Builder
    ): EndpointDetector {
        val instanceWebClient = instanceWebClientBuilder.build()
        val strategy = ChainingStrategy(
            QueryIndexEndpointStrategy(instanceWebClient, ApiMediaTypeHandler()),
            ProbeEndpointsStrategy(instanceWebClient, this.adminServerProperties.getProbedEndpoints())
        )
        
        return object : EndpointDetector(repository, strategy) {
            override fun detectEndpoints(id: InstanceId): Mono<Void> {
                return repository.find(id)
                    .flatMap { instance ->
                        if (isExternalService(instance)) {
                            Mono.empty()
                        } else {
                            super.detectEndpoints(id)
                        }
                    }
            }
        }
    }

    private fun isExternalService(instance: Instance): Boolean {
        val name = instance.registration.name
        val checkType = instance.registration.metadata["sba-check"]

        val isDatabase = name.endsWith("-db")
        val isObservability = name in setOf("jaeger", "jaeger-storage", "prometheus")

        return checkType == "kubernetes" || isDatabase || isObservability
    }

}
