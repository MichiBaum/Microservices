package com.michibaum.fitness_service

import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.fitness_service.fitbit.oauth.FitbitOAuthRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
@SpringBootTest(properties = [
    "fitbit.oauth.client-id=someClientId",
    "fitbit.oauth.client-secret=someClientSecret",
])
@TestcontainersConfiguration
class FitbitOAuthControllerIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var fitbitOAuthRepository: FitbitOAuthRepository

    @MockitoBean
    lateinit var jwsValidator: JwsValidator

    @Test
    fun `isUnauthorized`(){
        // GIVEN

        // WHEN
        mockMvc.get("/api/fitbit/token")
            .andExpect { status { isUnauthorized() } }
        // THEN

    }

    @Test
    fun `isOk`(){
        // GIVEN
        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())

        // WHEN
        mockMvc.get("/api/fitbit/token") {
            header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic3ViIjoiU29tZVVzZXJuYW1lIiwiZXhwIjoxNzMwNDA2MzM4LCJpYXQiOjE3MzAzNzc1MzgsInVzZXJJZCI6IjBmMzdjMTkyLTRjNTgtNGQyZS1iM2U5LTljNDMyZTk4ZWQzMSIsInBlcm1pc3Npb25zIjpbIkNIRVNTX1NFUlZJQ0UiLCJSRUdJU1RSWV9TRVJWSUNFIiwiQURNSU5fU0VSVklDRSJdfQ.FGEUdC0S7CveGnSSUMIPBGaOnzljBTpv1Nfs1dYHiocwCA5gSEY-_WkQJN3p18Nc0FsuGqNXAbJrLkRd_Mzc8W1OHIb7s5TW-klX1ePkhDxkNmzSpxpHTad04hjTiIeXmeCUb52S-9m_MqU5OBYlaWn8k2zBRe_P14cmUbMa2X3aXDxKkBmNmDS12ry0czPIz_20RPgpNuRyp9mk79uGlrUPNBbNpcodULgvjr9WGN32VAu5CNMtZM-wmpSz5e6YGq9S46OdPOWkRquS3vktJ4ikx7u4fKPNXWqkhpuGM81IlQu7OC2Dy8jKkc_6QUD4gnHqvLc51R8XXQfxWLqdrkzvXR6BqvOQ_iQvntR1VPjU15D4KUKUnBFRDWM_lUdJJRGKlq1xkS37AV96Jqqb9MlXfso57uenjdfrhaF5sCd7PJIEfHRUu0QhymnCyvxfWAmY3NBi_Fk6S-XXCKOcDXhKjAVE_xMkZzidMOwZ6GR9GPBbrDkL5VheozcGm_D9lq9klpFRdScAAg7mIsYnfmNMGzmW3l4TyNpTqTvKlu5tq_9dXgqGJgVQ3L_123eLJBNwSpKG0GHcPYtMZhJeqWG0rJ2cCsyOEfd7l8CPAzY3NNA-D289SJOJTJyOLssi3eDmPj8UISR5ClAg12jdhSsMGJYuLCnbm5DngCKh9XI")
        }.andExpect { status { isOk() } }
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
