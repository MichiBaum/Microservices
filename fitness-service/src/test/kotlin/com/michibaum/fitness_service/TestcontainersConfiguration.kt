package com.michibaum.fitness_service

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.*
import org.springframework.test.context.TestPropertySource
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(value = [FlywayClearDatabaseJunitExtension::class])
@Import(value = [TestcontainersTestConfiguration::class])
@TestPropertySource(properties = ["spring.flyway.clean-disabled=false"])
annotation class TestcontainersConfiguration()
