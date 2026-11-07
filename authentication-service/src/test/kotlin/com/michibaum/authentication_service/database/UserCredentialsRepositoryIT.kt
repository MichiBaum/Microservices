package com.michibaum.authentication_service.database

import com.michibaum.authentication_service.TestcontainersConfiguration
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
@TestcontainersConfiguration
class UserCredentialsRepositoryIT {

    @Autowired
    private lateinit var userCredentialsRepository: UserCredentialsRepository

    @Test
    fun `save and findByUsername persists and retrieves userId`() {
        val userId = UUID.randomUUID().toString()
        val username = "testuser"
        val credentials = UserCredentials(
            userId = userId,
            username = username,
            passwordHash = "someHash",
            enabled = true
        )

        val saved = userCredentialsRepository.save(credentials)
        assertNotNull(saved.id)
        assertEquals(userId, saved.userId)

        val retrieved = userCredentialsRepository.findByUsername(username)
        assertNotNull(retrieved)
        assertEquals(userId, retrieved?.userId)
        assertEquals(username, retrieved?.username)
        assertEquals("someHash", retrieved?.passwordHash)
        assertTrue(retrieved?.enabled ?: false)
    }
}
