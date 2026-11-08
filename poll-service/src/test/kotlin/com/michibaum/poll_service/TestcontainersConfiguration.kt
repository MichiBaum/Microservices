package com.michibaum.poll_service

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(value = [FlywayClearDatabaseJunitExtension::class])
@Import(value = [TestcontainersTestConfiguration::class])
@TestPropertySource(
    properties = [
        "spring.flyway.clean-disabled=false",
        "spring.boot.admin.service.username=admin_username",
        "spring.boot.admin.service.password=admin_password",
        "public-endpoints.package-names[0]=com.michibaum.poll_service"
    ]
)
annotation class TestcontainersConfiguration()
