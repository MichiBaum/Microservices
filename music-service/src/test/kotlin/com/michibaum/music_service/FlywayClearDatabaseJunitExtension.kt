package com.michibaum.music_service

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.beans.factory.getBean
import org.springframework.test.context.junit.jupiter.SpringExtension


class FlywayClearDatabaseJunitExtension: BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext) {
        val flyway = SpringExtension.getApplicationContext(context)
            .getBean<Flyway>()
        flyway.clean()
        flyway.migrate()
    }
}