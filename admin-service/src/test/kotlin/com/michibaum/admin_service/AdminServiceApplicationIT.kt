package com.michibaum.admin_service

import io.opentelemetry.sdk.trace.export.SpanExporter
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext


@SpringBootTest(properties = ["management.tracing.export.enabled=true", "management.opentelemetry.tracing.export.otlp.endpoint=http://localhost:4318/v1/traces"])
class AdminServiceApplicationIT {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Test
    fun contextLoads() {
        assertNotNull(applicationContext)
    }

    @Test
    fun otlpExporterIsConfigured() {
        val spanExporters = applicationContext.getBeansOfType(SpanExporter::class.java)
        assertNotNull(spanExporters)
        // At least one exporter should be an OTLP exporter if configured correctly
        // In SB 4.x, it's the OtlpHttpSpanExporter
        assert(spanExporters.values.any { it::class.java.simpleName.contains("Otlp") })
    }
}
