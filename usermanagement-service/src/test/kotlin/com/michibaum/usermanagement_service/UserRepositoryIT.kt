package com.michibaum.usermanagement_service

import com.michibaum.usermanagement_service.database.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

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

}