package com.michibaum.fitness_service

import com.michibaum.fitness_service.fitbit.oauth.FitbitOAuthProperties
import com.michibaum.fitness_service.fitbit.oauth.FitbitOAuthRepository
import com.michibaum.authentication_library.security.netty.JwsValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
@SpringBootTest
class FitbitOAuthControllerIT {

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var fitbitOAuthRepository: FitbitOAuthRepository

    @MockBean
    lateinit var fitbitOAuthProperties: FitbitOAuthProperties

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @Test
    fun `isUnauthorized`(){
        // GIVEN

        // WHEN
        webClient.get()
            .uri("/api/fitbit/token")
            .exchange()
            .expectStatus()
            .isUnauthorized()
        // THEN

    }

    @Test
    fun `isOk`(){
        // GIVEN
        `when`(jwsValidator.validate(anyString())).thenReturn(true)

        // WHEN
        webClient.get()
            .uri("/api/fitbit/token")
            .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic3ViIjoiU29tZVVzZXJuYW1lIiwiZXhwIjoxNzMwNDA2MzM4LCJpYXQiOjE3MzAzNzc1MzgsInVzZXJJZCI6IjBmMzdjMTkyLTRjNTgtNGQyZS1iM2U5LTljNDMyZTk4ZWQzMSIsInBlcm1pc3Npb25zIjpbIkNIRVNTX1NFUlZJQ0UiLCJSRUdJU1RSWV9TRVJWSUNFIiwiQURNSU5fU0VSVklDRSJdfQ.FGEUdC0S7CveGnSSUMIPBGaOnzljBTpv1Nfs1dYHiocwCA5gSEY-_WkQJN3p18Nc0FsuGqNXAbJrLkRd_Mzc8W1OHIb7s5TW-klX1ePkhDxkNmzSpxpHTad04hjTiIeXmeCUb52S-9m_MqU5OBYlaWn8k2zBRe_P14cmUbMa2X3aXDxKkBmNmDS12ry0czPIz_20RPgpNuRyp9mk79uGlrUPNBbNpcodULgvjr9WGN32VAu5CNMtZM-wmpSz5e6YGq9S46OdPOWkRquS3vktJ4ikx7u4fKPNXWqkhpuGM81IlQu7OC2Dy8jKkc_6QUD4gnHqvLc51R8XXQfxWLqdrkzvXR6BqvOQ_iQvntR1VPjU15D4KUKUnBFRDWM_lUdJJRGKlq1xkS37AV96Jqqb9MlXfso57uenjdfrhaF5sCd7PJIEfHRUu0QhymnCyvxfWAmY3NBi_Fk6S-XXCKOcDXhKjAVE_xMkZzidMOwZ6GR9GPBbrDkL5VheozcGm_D9lq9klpFRdScAAg7mIsYnfmNMGzmW3l4TyNpTqTvKlu5tq_9dXgqGJgVQ3L_123eLJBNwSpKG0GHcPYtMZhJeqWG0rJ2cCsyOEfd7l8CPAzY3NNA-D289SJOJTJyOLssi3eDmPj8UISR5ClAg12jdhSsMGJYuLCnbm5DngCKh9XI")
            .exchange()
            .expectStatus()
            .isOk()
        // THEN

        val all = fitbitOAuthRepository.findAll()
        assertEquals(1, all.size)
        assertNotEmpty(all[0].userId)
        assertNotEmpty(all[0].state)
        assertNotEmpty(all[0].codeVerifier)
        assertNotEmpty(all[0].codeChallenge)
        assertEquals("0f37c192-4c58-4d2e-b3e9-9c432e98ed31", all[0].userId)
    }

}
