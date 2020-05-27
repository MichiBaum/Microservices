package com.michibaum.lifemanagementbackend.config

import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource

class PropertyLogger : ApplicationListener<ApplicationPreparedEvent> {

    private val logger = KotlinLogging.logger {}

    override fun onApplicationEvent(event: ApplicationPreparedEvent) {
        logger.info("Starting logging application properties")
        printProperties(event.applicationContext.environment)
    }

    private fun printProperties(environment: ConfigurableEnvironment) {
        findPropertiesPropertySources(environment).forEach { propertySource ->
            logger.info("******* ${propertySource.name} *******")
            propertySource.propertyNames.let { propertyNames ->
                propertyNames.forEach { propertyName ->
                    val resolvedProperty = propertyName?.let { environment.getProperty(it) } ?: ""
                    val sourceProperty = propertyName?.let {
                        propertySource.getProperty(it)?.toString() ?: ""
                    } ?: ""
                    if (resolvedProperty == sourceProperty) {
                        logger.info("$propertyName=$resolvedProperty")
                    } else {
                        logger.info("$propertyName=$sourceProperty OVERRIDDEN to $resolvedProperty")
                    }
                }
            }
        }
    }

    private fun findPropertiesPropertySources(environment: ConfigurableEnvironment): List<EnumerablePropertySource<*>> =
        environment.propertySources.filterIsInstance<EnumerablePropertySource<*>>()
}
