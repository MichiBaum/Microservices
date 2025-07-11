package com.michibaum.gatewayservice.app.sitemapxml

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.annotation.Observed
import org.springframework.core.task.VirtualThreadTaskExecutor
import org.springframework.stereotype.Service
import java.util.concurrent.Callable

@Service
class SitemapXmlService(
    private val sitemapXmlProperties: SitemapXmlProperties,
    private val dataLocationsFetcher: DataLocationsFetcher,
    private val observationRegistry: ObservationRegistry
) {

    private val taskExecutor = VirtualThreadTaskExecutor("sitemap-data-loader-")

    @Observed(name = "sitemap-xml-service-generate")
    fun generateWith(host: String): String {
        observationRegistry.currentObservation?.highCardinalityKeyValue("host", host)
        
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

        val dataLocationResults = sitemapXmlProperties.dataLocations.map { location ->
            taskExecutor.submitCompletable( Callable{
                val observation = Observation.start("sitemap-xml-data-fetch", this.observationRegistry)
                observation.lowCardinalityKeyValue("sitemap-xml-data-fetch-location", location.dataLocation.name)
                val data = dataLocationsFetcher.fetch(location.dataLocation)
                data.map { id ->
                    """
                        <url>
                            <loc>${baseUrl + location.staticPart.replace("{replace}", id)}</loc>
                        </url>
                    """
                }
                .also { observation.stop() }
            })
        }.map { it.join() } // Wait for all tasks to complete and collect results


        // Append the generated results to the XML builder
        dataLocationResults.flatten().forEach { urlEntry ->
            xmlBuilder.appendLine(urlEntry.trimIndent())
        }

        // Close the XML
        xmlBuilder.appendLine("</urlset>")

        return xmlBuilder.toString()
    }

}