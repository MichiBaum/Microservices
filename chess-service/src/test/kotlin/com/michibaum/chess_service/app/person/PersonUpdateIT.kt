package com.michibaum.chess_service.app.person

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.Gender
import com.michibaum.chess_service.database.Person
import com.michibaum.chess_service.database.PersonRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.put
import java.util.*

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@TestcontainersConfiguration
class PersonUpdateIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var personRepository: PersonRepository

    @Test
    fun updatePersonShouldUpdateAnExistingPerson() {
        val existingPerson = personRepository.save(Person(firstname = "Original", lastname = "Name", federation = null, birthday = null, gender = Gender.MALE, accounts = emptySet()))
        val personId = existingPerson.idOrThrow().toString()

        val updatedPersonDto = WritePersonDto(
            firstname = "Updated",
            lastname = "Name",
            federation = "GER",
            birthday = "1985-12-31",
            gender = Gender.MALE
        )

        mockMvc.put("/api/persons/$personId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updatedPersonDto)
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$.id") { value(personId) }
                jsonPath("$.firstname") { value("Updated") }
                jsonPath("$.lastname") { value("Name") }
                jsonPath("$.federation") { value("GER") }
                jsonPath("$.birthday") { value("1985-12-31") }
                jsonPath("$.gender") { value(Gender.MALE.toString()) }
            }
        }

        val updatedPerson = personRepository.findById(UUID.fromString(personId)).get()
        assertEquals("Updated", updatedPerson.firstname)
    }

    @Test
    fun updatePersonShouldReturnNotFoundForNonExistentPerson() {
        val randomId = UUID.randomUUID().toString()
        val updatedPersonDto = WritePersonDto(
            firstname = "Updated",
            lastname = "Name",
            gender = Gender.MALE
        )

        mockMvc.put("/api/persons/$randomId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updatedPersonDto)
        }.andExpect {
            status { isNotFound() }
        }
    }
}
