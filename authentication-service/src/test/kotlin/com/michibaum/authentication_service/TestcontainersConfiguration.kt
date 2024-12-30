package com.michibaum.authentication_service

import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(value = [TestcontainersTestConfiguration::class])
annotation class TestcontainersConfiguration()
