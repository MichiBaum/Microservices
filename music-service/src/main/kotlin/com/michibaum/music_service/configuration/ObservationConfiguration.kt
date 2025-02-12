package com.michibaum.admin_service.configuration

import io.micrometer.tracing.exporter.FinishedSpan
import io.micrometer.tracing.exporter.SpanExportingPredicate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ObservationConfiguration {

    @Bean
    fun spanExportingPredicate(): SpanExportingPredicate {
        return SpanExportingPredicate { span: FinishedSpan ->
            val statusCode = span.tags["http.status_code"]?.toIntOrNull()
            val isClientOrServerErrorCode = statusCode != null && statusCode >= 400
            val hasErrorTag = !span.tags["error"].isNullOrEmpty()
            val hasExceptionTag = !span.tags["exception"].isNullOrEmpty()
            val hasError = span.error != null
            val isError = isClientOrServerErrorCode || hasErrorTag || hasExceptionTag || hasError

            // If this is an error span, export it
            if (isError) {
                return@SpanExportingPredicate true
            }

            // Is Actuator or Eureka
            val isActuatorUri = span.tags["uri"].orEmpty().startsWith("/actuator")
            val isEurekaUri = span.tags["uri"].orEmpty().startsWith("/eureka")
            val containsEurekaUrl = span.tags["http.url"].orEmpty().contains("/eureka")
            !(isActuatorUri || isEurekaUri || containsEurekaUrl)
        }
    }

}