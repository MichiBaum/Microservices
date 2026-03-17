package com.michibaum.chess_service.app.person

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.Gender
import com.michibaum.chess_service.database.Person
import com.michibaum.chess_service.database.PersonRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@TestcontainersConfiguration
class PersonSearchIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var personRepository: PersonRepository

    @Test
    fun findPersonShouldReturnPersonsMatchingCriteria() {
        personRepository.save(Person(firstname = "John", lastname = "Doe", federation = null, birthday = null, gender = Gender.MALE, accounts = emptySet()))
        personRepository.save(Person(firstname = "Jane", lastname = "Doe", federation = null, birthday = null, gender = Gender.FEMALE, accounts = emptySet()))
        personRepository.save(Person(firstname = "Max", lastname = "Mustermann", federation = null, birthday = null, gender = Gender.MALE, accounts = emptySet()))

        val searchDto = PersonSearchDto(firstname = "John", lastname = "")

        mockMvc.post("/api/persons/search") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(searchDto)
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$.length()") { value(1) }
                jsonPath("$[0].firstname") { value("John") }
            }
        }
    }

    @Test
    fun findPersonShouldReturnMultiplePersonsMatchingCriteria() {
        personRepository.save(Person(firstname = "John", lastname = "Doe", federation = null, birthday = null, gender = Gender.MALE, accounts = emptySet()))
        personRepository.save(Person(firstname = "Jane", lastname = "Doe", federation = null, birthday = null, gender = Gender.FEMALE, accounts = emptySet()))

        val searchDto = PersonSearchDto(firstname = "", lastname = "Doe")

        mockMvc.post("/api/persons/search") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(searchDto)
        }.andExpect {
            status { isOk() }
            content {
                jsonPath("$.length()") { value(2) }
                jsonPath("$[?(@.firstname == 'John')].lastname") { value("Doe") }
                jsonPath("$[?(@.firstname == 'Jane')].lastname") { value("Doe") }
            }
        }
    }
}
