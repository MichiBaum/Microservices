package com.michibaum.fitness_service

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(value = [FlywayClearDatabaseJunitExtension::class])
@Import(value = [TestcontainersTestConfiguration::class])
@TestPropertySource(properties = ["spring.flyway.clean-disabled=false"])
annotation class TestcontainersConfiguration()
