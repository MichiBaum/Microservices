package com.michibaum.lifemanagementbackend.core.config

import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import java.util.*

class PropertyLogger: ApplicationListener<ApplicationPreparedEvent> {

    private val logger = KotlinLogging.logger {}

    override fun onApplicationEvent(event: ApplicationPreparedEvent) {
        logger.info("Starting logging application properties")
        val environment = event.applicationContext.environment
        printProperties(environment)
    }

    private fun printProperties(environment: ConfigurableEnvironment) {
        for (propertySource in findPropertiesPropertySources(environment)) {
            logger.info("******* ${propertySource.name} *******")
            val propertyNames = propertySource.propertyNames
            Arrays.sort(propertyNames)
            for (propertyName in propertyNames) {
                val resolvedProperty = propertyName?.let { environment.getProperty(it) } ?: ""
                val sourceProperty = propertyName?.let { _propertyName -> propertySource.getProperty(_propertyName)?.toString() ?:"" } ?:""
                if (resolvedProperty == sourceProperty) {
                    logger.info("$propertyName=$resolvedProperty")
                } else {
                    logger.info("$propertyName=$sourceProperty OVERRIDDEN to $resolvedProperty")
                }
            }
        }
    }

    private fun findPropertiesPropertySources(environment: ConfigurableEnvironment): List<EnumerablePropertySource<*>> {
        val propertiesPropertySources: MutableList<EnumerablePropertySource<*>> =
            LinkedList()
        for (propertySource in environment.propertySources) {
            if (propertySource is EnumerablePropertySource<*>) {
                propertiesPropertySources.add(propertySource)
            }
        }
        return propertiesPropertySources
    }
}
