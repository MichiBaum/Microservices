package com.michibaum.fitness_service

import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.*
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Target(CLASS)
@Retention(RUNTIME)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Import(value = [TestcontainersTestConfiguration::class])
annotation class TestcontainersConfiguration()
