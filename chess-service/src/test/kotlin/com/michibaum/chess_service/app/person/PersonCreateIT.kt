package com.michibaum.chess_service.app.person

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.Gender
import com.michibaum.chess_service.database.PersonRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.put

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@TestcontainersConfiguration
class PersonCreateIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var personRepository: PersonRepository

    @Test
    fun createPersonShouldCreateANewPerson() {
        val personDto = WritePersonDto(
            firstname = "Alice",
            lastname = "Smith",
            federation = "GER",
            birthday = "1990-01-01",
            gender = Gender.FEMALE
        )

        mockMvc.put("/api/persons") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(personDto)
        }.andExpect {
            status { isCreated() }
            content {
                jsonPath("$.firstname") { value("Alice") }
                jsonPath("$.lastname") { value("Smith") }
                jsonPath("$.federation") { value("GER") }
                jsonPath("$.birthday") { value("1990-01-01") }
                jsonPath("$.gender") { value(Gender.FEMALE.toString()) }
            }
        }

        assertEquals(1, personRepository.findAll().filter { it.firstname == "Alice" }.size)
    }

    @Test
    fun createPersonShouldReturnBadRequestForInvalidInput() {
        val personDto = WritePersonDto(
            firstname = "Alice123", // Invalid: contains numbers
            lastname = "Smith",
            gender = Gender.FEMALE
        )

        mockMvc.put("/api/persons") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(personDto)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
