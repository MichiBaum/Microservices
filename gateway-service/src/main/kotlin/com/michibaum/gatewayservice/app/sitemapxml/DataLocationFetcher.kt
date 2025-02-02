package com.michibaum.gatewayservice.app.sitemapxml

interface DataLocationFetcher {
    fun supports(dataLocation: DataLocation): Boolean
    fun fetch(): List<String>
}