package com.michibaum.gatewayservice.app.sitemapxml

import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.annotation.Observed
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerResponse

@Component
class SitemapXmlController(
    private val sitemapXmlService: SitemapXmlService,
    private val observationRegistry: ObservationRegistry
) {

    @Observed(name = "sitemap-xml-controller-get")
    fun sitemapXml(): HandlerFunction<ServerResponse> {
        return HandlerFunction { request ->
            val host = request.headers().firstHeader("Host")
            observationRegistry.currentObservation?.highCardinalityKeyValue("host", host ?: "unknown")
            
            if (host == null)
                return@HandlerFunction ServerResponse.badRequest().body("The 'Host' header is missing. Please include it in the request.")

            val content = sitemapXmlService.generateWith(host)

            ServerResponse.ok()
                .header("Content-Type", "text/xml")
                .body(content)
        }

    }

}