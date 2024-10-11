package com.michibaum.chess_service.app

import com.michibaum.chess_service.domain.PersonProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class PersonRepositoryIT {

    @Autowired
    lateinit var personRepository: PersonRepository

    @Test
    fun `insert person without accounts`(){
        // GIVEN
        val player = PersonProvider.person()

        // WHEN
        val result = personRepository.save(player)

        // THEN
        assertNotNull(result.id)
        assertEquals(player.firstname, result.firstname)
        assertEquals(player.lastname, result.lastname)
        assertNotNull(result.accounts)
        assertEquals(0, result.accounts.size)
    }

}