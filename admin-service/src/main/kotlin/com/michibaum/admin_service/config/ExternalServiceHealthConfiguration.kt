package com.michibaum.admin_service.config

import com.michibaum.admin_service.app.kubernetes.KubernetesClusterService
import de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration

@Configuration
class ExternalServiceHealthConfiguration(
    private val kubernetesClusterService: KubernetesClusterService
) {

    @Bean
    fun externalServiceHealthFilter(): InstanceExchangeFilterFunction {
        return InstanceExchangeFilterFunction { instance, request, next ->
            val name = instance.registration.name
            val path = request.url().path

            val isDatabase = name.endsWith("-db")
            val isObservability = name in setOf("jaeger", "jaeger-storage", "prometheus")

            if ((isDatabase || isObservability) && path.endsWith("/health")) {
                return@InstanceExchangeFilterFunction Mono.fromCallable {
                    kubernetesClusterService.isServiceUp(name)
                }.subscribeOn(Schedulers.boundedElastic())
                    .flatMap { isUp ->
                        val status = if (isUp) "UP" else "OFFLINE"
                        val httpStatus = if (isUp) HttpStatus.OK else HttpStatus.SERVICE_UNAVAILABLE

                        Mono.just(
                            ClientResponse.create(httpStatus)
                                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .body("{\"status\":\"$status\"}")
                                .build()
                        )
                    }
                    .timeout(Duration.ofSeconds(5), Mono.just(
                        ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE)
                            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .body("{\"status\":\"OFFLINE\", \"details\": \"Kubernetes health check timed out\"}")
                            .build()
                    ))
            }
            next.exchange(request)
        }
    }
}
