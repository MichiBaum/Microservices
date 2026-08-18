package com.michibaum.observability_starter

import io.micrometer.observation.ObservationRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class ObservabilityAutoConfigurationTest {

    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ObservabilityAutoConfiguration::class.java))

    @Test
    fun `should not fail if ObservationRegistry is missing`() {
        contextRunner.run { context ->
            assertThat(context).hasNotFailed()
            assertThat(context).doesNotHaveBean(ServiceObservationAspect::class.java)
        }
    }

    @Test
    fun `should provide beans when ObservationRegistry is present`() {
        contextRunner
            .withUserConfiguration(TestConfig::class.java)
            .run { context ->
                assertThat(context).hasSingleBean(ServiceObservationAspect::class.java)
            }
    }

    @Test
    fun `should not provide ServiceObservationAspect when disabled by property`() {
        contextRunner
            .withUserConfiguration(TestConfig::class.java)
            .withPropertyValues("management.observations.services.enabled=false")
            .run { context ->
                assertThat(context).doesNotHaveBean(ServiceObservationAspect::class.java)
            }
    }

    @Configuration
    class TestConfig {
        @Bean
        fun observationRegistry(): ObservationRegistry = ObservationRegistry.create()
    }
}
