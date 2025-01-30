package com.michibaum.gatewayservice.app.sitemapxml

import org.springframework.stereotype.Service
import java.io.StringWriter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@Service
class SitemapXmlService(
    private val sitemapXmlProperties: SitemapXmlProperties,
    private val dataLocationsFetcher: DataLocationsFetcher
) {

    private val executor = Executors.newVirtualThreadPerTaskExecutor()

    fun generateWith(host: String): String {
        // Sitemap size limits: All formats limit a single sitemap to 50MB (uncompressed) or 50,000 URLs.
        val baseUrl = "https://$host"
        val xmlBuilder = StringBuilder()

        // Start building the XML
        xmlBuilder.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        xmlBuilder.appendLine("""<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">""")

        // Static locations
        sitemapXmlProperties.locations.forEach {
            xmlBuilder.appendLine("""
               <url>
                   <loc>$baseUrl$it</loc>
               </url>
           """.trimIndent())
        }

        // Process dynamic data locations asynchronously using virtual threads
        val dataLocationResults = sitemapXmlProperties.dataLocations.map { location ->
            CompletableFuture.supplyAsync({
                val data = dataLocationsFetcher.fetch(location.dataLocation) // Fetch the data
                data.map { id ->
                    """  <url>
                        <loc>${baseUrl + location.staticPart.replace("{replace}", id)}</loc>
                      </url>
                    """
                }
            }, executor)
        }.map { it.join() } // Wait for all tasks to complete and collect results

        // Append the generated results to the XML builder
        dataLocationResults.flatten().forEach { urlEntry ->
            xmlBuilder.appendLine(urlEntry.trimIndent())
        }

        // Close the XML
        xmlBuilder.appendLine("</urlset>")
        executor.close() // Manually shut down the virtual thread executor

        return xmlBuilder.toString()
    }

}