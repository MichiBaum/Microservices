package com.michibaum.gatewayservice.app.sitemapxml

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class SitemapXmlController(
    private val sitemapXmlService: SitemapXmlService
) {

    @GetMapping("/sitemap.xml", produces = ["text/xml"])
    fun sitemapXml(@RequestHeader("Host") host: String?): ResponseEntity<String> {
        if (host == null)
            return ResponseEntity.internalServerError().build()

        val sitemap = sitemapXmlService.generateWith(host)
        return ResponseEntity.ok(sitemap)
    }

}