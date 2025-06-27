package com.michibaum.alexandria_service.app

import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.owasp.html.Sanitizers
import org.springframework.stereotype.Component

@Component
class TextSanitizer {
    
    val sanitizer = Sanitizers.FORMATTING
        .and(Sanitizers.LINKS)
        .and(Sanitizers.BLOCKS)
        .and(Sanitizers.TABLES)
        .and(Sanitizers.STYLES)
        .and(Sanitizers.IMAGES)
    
    val jsoupSanitizer = Safelist.basic()
        .addTags("h1", "h2", "h3")
        .addAttributes("a", "target")
        .addProtocols("a", "href", "http", "https");
    
    fun sanitize(text: String): String = text.apply {
        sanitizer.sanitize(this)
    }.apply {
        Jsoup.clean(this, jsoupSanitizer)
    }
}