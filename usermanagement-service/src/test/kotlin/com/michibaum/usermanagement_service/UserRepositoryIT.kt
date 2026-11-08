package com.michibaum.usermanagement_service

import com.michibaum.usermanagement_service.database.User
import com.michibaum.usermanagement_service.database.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.jdbc.core.JdbcTemplate

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestcontainersConfiguration
class UserRepositoryIT {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `find with no database content`(){
        // GIVEN


        // WHEN
        val result = userRepository.findAll()

        // THEN
        assertEquals(0, result.size)
    }

    @Test
    fun `save and retrieve user`() {
        val user = User(email = "test@example.com")
        val saved = userRepository.save(user)

        assertNotNull(saved.id)
        assertEquals("test@example.com", saved.email)

        val retrieved = userRepository.findById(saved.id!!).orElse(null)
        assertNotNull(retrieved)
        assertEquals("test@example.com", retrieved.email)

        assertTrue(userRepository.existsByEmail("test@example.com"))
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"))
    }

    @Test
    fun `multiple users can be persisted`() {
        val user1 = userRepository.save(User(email = "u1@example.com"))
        val user2 = userRepository.save(User(email = "u2@example.com"))

        assertNotNull(user1.id)
        assertNotNull(user2.id)
        assertNotEquals(user1.id, user2.id)

        val all = userRepository.findAll()
        assertTrue(all.any { it.email == "u1@example.com" })
        assertTrue(all.any { it.email == "u2@example.com" })
    }

    @Test
    fun `schema should not contain username or password columns in user table`() {
        val columns = jdbcTemplate.queryForList(
            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'user' AND TABLE_SCHEMA = DATABASE()",
            String::class.java
        )
        assertFalse(columns.any { it.equals("username", ignoreCase = true) })
        assertFalse(columns.any { it.equals("password", ignoreCase = true) })
        assertTrue(columns.any { it.equals("id", ignoreCase = true) })
        assertTrue(columns.any { it.equals("email", ignoreCase = true) })
    }
}