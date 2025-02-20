package com.michibaum.gatewayservice.app.sitemapxml

import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerResponse

@Component
class SitemapXmlController(
    private val sitemapXmlService: SitemapXmlService
) {

    fun sitemapXml(): HandlerFunction<ServerResponse> {
        return HandlerFunction { request ->
            val host = request.headers().firstHeader("Host")
                ?: return@HandlerFunction ServerResponse.badRequest().body("The 'Host' header is missing. Please include it in the request.")

            val content = sitemapXmlService.generateWith(host)

            ServerResponse.ok()
                .header("Content-Type", "text/xml")
                .body(content)
        }

    }

}