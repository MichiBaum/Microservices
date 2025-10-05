package com.michibaum.websiteservice.config

import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.get
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.CachingResourceResolver
import org.springframework.web.servlet.resource.CachingResourceTransformer
import org.springframework.web.servlet.resource.PathResourceResolver
import java.io.IOException
import java.time.Duration

@Configuration
class AngularConfig (
    private val cacheManager: CacheManager,
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val staticResourceCache = cacheManager["staticResourceCache"]!!
        val staticResourceTransformCache = cacheManager["staticResourceTransformCache"]!!
        
        registry.addResourceHandler("/chunk-*.js", "/styles-*.css", "/polyfills-*.js", "/main-*.js")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(
                CacheControl.maxAge(Duration.ofDays(365))
                    .cachePublic()
                    .noTransform()
            )
            .resourceChain(true)
            .addResolver(CachingResourceResolver(staticResourceCache))
            .addTransformer(CachingResourceTransformer(staticResourceTransformCache))
            .addResolver(PathResourceResolver())

        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(
                CacheControl.maxAge(Duration.ofMinutes(5))
                    .mustRevalidate()
                    .cachePublic()
            )
            .resourceChain(true)
            .addResolver(CachingResourceResolver(staticResourceCache))
            .addTransformer(CachingResourceTransformer(staticResourceTransformCache))
            .addResolver(object : PathResourceResolver() {
                @Throws(IOException::class)
                override fun getResource(resourcePath: String, location: Resource): Resource {
                    val requestedResource = location.createRelative(resourcePath)
                    return if (requestedResource.exists() && requestedResource.isReadable)
                        requestedResource
                    else
                        ClassPathResource("/static/index.html")
                }
            })
    }
}