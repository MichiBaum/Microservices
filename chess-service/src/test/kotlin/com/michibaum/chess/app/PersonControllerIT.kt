package com.michibaum.chess.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.chess.domain.Person
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@ExtendWith(SpringExtension::class)
@WebFluxTest(PersonController::class)
class PersonControllerIT {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var personService: PersonService

    @MockBean
    lateinit var personConverter: PersonConverter

    @Test
    fun `should create a person when valid data is provided`() {
        val requestPersonDto = PersonDto(
            null,
            "John",
            "Doe"
        )
        val convertedPerson = Person(
            firstname = requestPersonDto.firstname,
            lastname = requestPersonDto.lastname,
            accounts = emptySet()
        )
        val savedPerson = Person(
            id = UUID.randomUUID(),
            firstname = convertedPerson.firstname,
            lastname = convertedPerson.lastname,
            accounts = emptySet()
        )
        val responsePersonDto = PersonDto(
            id = savedPerson.id,
            firstname = savedPerson.firstname,
            lastname = savedPerson.lastname
        )

        `when`(personConverter.convert(requestPersonDto)).thenReturn(convertedPerson)
        `when`(personService.savePerson(convertedPerson)).thenReturn(savedPerson)
        `when`(personConverter.convert(savedPerson)).thenReturn(responsePersonDto)

        webTestClient.post()
            .uri("/api/persons")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestPersonDto)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .json(objectMapper.writeValueAsString(responsePersonDto))

        verify(personConverter).convert(requestPersonDto)
        verify(personService).savePerson(convertedPerson)
        verify(personConverter).convert(savedPerson)
    }

}
