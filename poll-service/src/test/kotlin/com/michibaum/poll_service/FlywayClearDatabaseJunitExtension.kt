package com.michibaum.poll_service

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension

class FlywayClearDatabaseJunitExtension: BeforeEachCallback {

    override fun beforeEach(context: ExtensionContext) {
        val flyway = SpringExtension.getApplicationContext(context)
            .getBean(Flyway::class.java)

        flyway.clean()
        flyway.migrate()
    }
}
