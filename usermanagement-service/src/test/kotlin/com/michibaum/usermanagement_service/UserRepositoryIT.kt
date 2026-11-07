package com.michibaum.usermanagement_service

import com.michibaum.usermanagement_service.database.User
import com.michibaum.usermanagement_service.database.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestcontainersConfiguration
class UserRepositoryIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `find with no database content`(){
        // GIVEN


        // WHEN
        var result = userRepository.findAll()

        // THEN
        assertEquals(0, result.size)

    }

    @Test
    fun `save and retrieve user with null username and password`() {
        val user = User(email = "test@example.com")
        val saved = userRepository.save(user)

        assertNotNull(saved.id)
        assertNull(saved.username)
        assertNull(saved.password)
        assertEquals("test@example.com", saved.email)

        val retrieved = userRepository.findById(saved.id!!).orElse(null)
        assertNotNull(retrieved)
        assertNull(retrieved.username)
        assertNull(retrieved.password)
        assertEquals("test@example.com", retrieved.email)

        assertTrue(userRepository.existsByEmail("test@example.com"))
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"))
    }

    @Test
    fun `multiple users with null username can be persisted`() {
        val user1 = userRepository.save(User(email = "u1@example.com"))
        val user2 = userRepository.save(User(email = "u2@example.com"))

        assertNotNull(user1.id)
        assertNotNull(user2.id)
        assertNotEquals(user1.id, user2.id)
        assertNull(user1.username)
        assertNull(user2.username)

        val all = userRepository.findAll()
        assertTrue(all.any { it.email == "u1@example.com" })
        assertTrue(all.any { it.email == "u2@example.com" })
    }
}