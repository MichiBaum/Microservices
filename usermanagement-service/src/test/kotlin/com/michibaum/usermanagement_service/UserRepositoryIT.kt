package com.michibaum.usermanagement_service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
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