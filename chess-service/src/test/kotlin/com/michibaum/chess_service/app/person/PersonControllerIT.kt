package com.michibaum.chess_service.app.person

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.chess_service.domain.Gender
import com.michibaum.chess_service.domain.Person
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@AutoConfigureWebTestClient
@SpringBootTest
class PersonControllerIT {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var personService: PersonService

    @MockitoBean
    lateinit var personConverter: PersonConverter

    @MockitoBean
    lateinit var jwsValidator: JwsValidator

    @Test
    fun `should create a person when valid data is provided`() {
        // GIVEN
        val requestPersonDto = WritePersonDto(
            firstname = "John",
            lastname = "Doe",
            fideId = "121212",
            gender = Gender.MALE,
        )
        val convertedPerson = Person(
            firstname = requestPersonDto.firstname,
            lastname = requestPersonDto.lastname,
            fideId = requestPersonDto.fideId,
            gender = Gender.MALE,
            accounts = emptySet(),
            federation = null,
            birthday = null,
        )
        val savedPerson = Person(
            id = UUID.randomUUID(),
            firstname = convertedPerson.firstname,
            lastname = convertedPerson.lastname,
            fideId = convertedPerson.fideId,
            accounts = emptySet(),
            federation = null,
            birthday = null,
            gender = Gender.MALE
        )
        val responsePersonDto = PersonDto(
            id = savedPerson.idOrThrow(),
            firstname = savedPerson.firstname,
            lastname = savedPerson.lastname,
            fideId = savedPerson.fideId,
            federation = savedPerson.federation,
            birthday = savedPerson.birthday.toString(),
            gender = savedPerson.gender,
            accounts = emptySet(),
        )

        `when`(personService.savePerson(convertedPerson)).thenReturn(savedPerson)
        `when`(personConverter.convert(savedPerson)).thenReturn(responsePersonDto)
        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())


        // WHEN
        webTestClient.put()
            .uri("/api/persons")
            .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic3ViIjoiU29tZVVzZXJuYW1lIiwiZXhwIjoxNzMwNDA2MzM4LCJpYXQiOjE3MzAzNzc1MzgsInVzZXJJZCI6IjBmMzdjMTkyLTRjNTgtNGQyZS1iM2U5LTljNDMyZTk4ZWQzMSIsInBlcm1pc3Npb25zIjpbIkNIRVNTX1NFUlZJQ0UiLCJSRUdJU1RSWV9TRVJWSUNFIiwiQURNSU5fU0VSVklDRSJdfQ.FGEUdC0S7CveGnSSUMIPBGaOnzljBTpv1Nfs1dYHiocwCA5gSEY-_WkQJN3p18Nc0FsuGqNXAbJrLkRd_Mzc8W1OHIb7s5TW-klX1ePkhDxkNmzSpxpHTad04hjTiIeXmeCUb52S-9m_MqU5OBYlaWn8k2zBRe_P14cmUbMa2X3aXDxKkBmNmDS12ry0czPIz_20RPgpNuRyp9mk79uGlrUPNBbNpcodULgvjr9WGN32VAu5CNMtZM-wmpSz5e6YGq9S46OdPOWkRquS3vktJ4ikx7u4fKPNXWqkhpuGM81IlQu7OC2Dy8jKkc_6QUD4gnHqvLc51R8XXQfxWLqdrkzvXR6BqvOQ_iQvntR1VPjU15D4KUKUnBFRDWM_lUdJJRGKlq1xkS37AV96Jqqb9MlXfso57uenjdfrhaF5sCd7PJIEfHRUu0QhymnCyvxfWAmY3NBi_Fk6S-XXCKOcDXhKjAVE_xMkZzidMOwZ6GR9GPBbrDkL5VheozcGm_D9lq9klpFRdScAAg7mIsYnfmNMGzmW3l4TyNpTqTvKlu5tq_9dXgqGJgVQ3L_123eLJBNwSpKG0GHcPYtMZhJeqWG0rJ2cCsyOEfd7l8CPAzY3NNA-D289SJOJTJyOLssi3eDmPj8UISR5ClAg12jdhSsMGJYuLCnbm5DngCKh9XI")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestPersonDto)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .json(objectMapper.writeValueAsString(responsePersonDto))

        // THEN
        verify(personService).savePerson(convertedPerson)
        verify(personConverter).convert(savedPerson)
    }

}
