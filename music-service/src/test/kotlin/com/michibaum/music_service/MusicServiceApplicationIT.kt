package com.michibaum.music_service

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
@TestcontainersConfiguration
class MusicServiceApplicationIT{
    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Test
    fun contextLoads() {
        assertNotNull(applicationContext)
    }
}