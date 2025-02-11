package com.michibaum.gatewayservice.config

import io.micrometer.tracing.exporter.FinishedSpan
import io.micrometer.tracing.exporter.SpanExportingPredicate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ObservationConfiguration {

    @Bean
    fun noActuatorSpanPredicate(): SpanExportingPredicate {
        return SpanExportingPredicate { span: FinishedSpan ->
            !span.tags["uri"].orEmpty().startsWith("/actuator")
        }
    }

    @Bean
    fun noEurekaSpanPredicate(): SpanExportingPredicate {
        return SpanExportingPredicate { span: FinishedSpan ->
            !span.tags["uri"].orEmpty().startsWith("/eureka")
        }
    }

}