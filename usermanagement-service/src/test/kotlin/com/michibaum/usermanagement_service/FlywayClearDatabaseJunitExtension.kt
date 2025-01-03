package com.michibaum.usermanagement_service

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.test.context.junit.jupiter.SpringExtension


class FlywayClearDatabaseJunitExtension: BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        if(context == null) throw IllegalArgumentException("Context must not be null")

        val flyway = SpringExtension.getApplicationContext(context)
            .getBean(Flyway::class.java)
        flyway.clean()
        flyway.migrate()
    }
}