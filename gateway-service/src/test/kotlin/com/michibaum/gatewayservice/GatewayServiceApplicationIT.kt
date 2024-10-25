package com.michibaum.gatewayservice

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class GatewayServiceApplicationIT {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Test
    fun contextLoads() {
        assertNotNull(applicationContext)
    }
}