package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.Gender
import com.michibaum.chess_service.database.Person
import com.michibaum.chess_service.database.PersonRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@TestcontainersConfiguration
class PersonsGetIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var personRepository: PersonRepository

    @Test
    fun personsShouldReturnAllPersons() {
        val person1 = personRepository.save(Person(firstname = "John", lastname = "Doe", federation = null, birthday = null, gender = Gender.MALE, accounts = emptySet()))
        val person2 = personRepository.save(Person(firstname = "Jane", lastname = "Doe", federation = null, birthday = null, gender = Gender.FEMALE, accounts = emptySet()))

        mockMvc.get("/api/persons") {
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$[?(@.firstname == 'John')].lastname") { value("Doe") }
                jsonPath("$[?(@.firstname == 'Jane')].lastname") { value("Doe") }
            }
        }
    }
}
