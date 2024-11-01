package com.michibaum.authentication_library

import com.michibaum.permission_library.Permissions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class JwsWrapperUT{

    @Test
    fun `getUserId is correct`(){
        // GIVEN
        val token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic3ViIjoiU29tZVVzZXJuYW1lIiwiZXhwIjoxNzMwNDAyMzc5LCJpYXQiOjE3MzAzNzM1NzksInVzZXJJZCI6IjBmMzdjMTkyLTRjNTgtNGQyZS1iM2U5LTljNDMyZTk4ZWQzMSIsInBlcm1pc3Npb25zIjpbIkNIRVNTX1NFUlZJQ0UiLCJSRUdJU1RSWV9TRVJWSUNFIiwiQURNSU5fU0VSVklDRSJdfQ.pPYVies5vOWkP96ZLs_M9p0WLe85ery0Vr875MRHHQ5PtjwGU6YWCGX26-i-S8OJ9I5-ava_K3N5kjFJ2dkklEz7GYXnqaL_-QZa14SALTz3vfkx8dTt1CvTzf8_ImEckdbOoiK7iOHnu_kriuLCvJ7G4ep9u0wtaFohXLImhA0deKsHdlFaSfr7nHOy20XPIru0BIGf_N4w8xUhct8bC4ZoQzdHjxo447h1IXwjLSo890chVBU_dHLLi53aCUA7EG4iW9ptmh0BRkpMAf4FO2hFSCT8khGFbg2qHNTF_3GGK51SVIcXzs34SLtDbJZMiVVeCjUgYNTEo5cf58fhh8bBQg6wsHyzjoOS6tSRSdVC0X7znzzpTMIuRrDr9D3_keBh7QvDZddHimB0vs8NTmYSPXJTUdVThNE5l_XLyMXWeAZ_zl9e_VfOySl60ITomu1vVwqszcSbniCs6K8iCxrZDKcpHoW4nrydWQfkw_ruYjCEXaOlphrEyj8tspilDy4nYjGkZCoMygVOx4jJDw1xQYOXmcEFXU4X3GII27wTEv4u8JUpqq_uNnKpRh7-fEjTOk02maxttzXB4iCLgKgatmeLs-HxKwUMBcSJ559CQ-zR_oAygBfObwogx6vJn4JjJDb7ilfLhlpxhUZIFiybp8cIZgh0MA_3moLu5Ro"

        // WHEN
        val jwsWrapper = JwsWrapper(token)

        // THEN
        assertEquals("0f37c192-4c58-4d2e-b3e9-9c432e98ed31", jwsWrapper.getUserId())

    }

    @Test
    fun `getUsername is correct`(){
        // GIVEN
        val token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic3ViIjoiU29tZVVzZXJuYW1lIiwiZXhwIjoxNzMwNDAyMzc5LCJpYXQiOjE3MzAzNzM1NzksInVzZXJJZCI6IjBmMzdjMTkyLTRjNTgtNGQyZS1iM2U5LTljNDMyZTk4ZWQzMSIsInBlcm1pc3Npb25zIjpbIkNIRVNTX1NFUlZJQ0UiLCJSRUdJU1RSWV9TRVJWSUNFIiwiQURNSU5fU0VSVklDRSJdfQ.pPYVies5vOWkP96ZLs_M9p0WLe85ery0Vr875MRHHQ5PtjwGU6YWCGX26-i-S8OJ9I5-ava_K3N5kjFJ2dkklEz7GYXnqaL_-QZa14SALTz3vfkx8dTt1CvTzf8_ImEckdbOoiK7iOHnu_kriuLCvJ7G4ep9u0wtaFohXLImhA0deKsHdlFaSfr7nHOy20XPIru0BIGf_N4w8xUhct8bC4ZoQzdHjxo447h1IXwjLSo890chVBU_dHLLi53aCUA7EG4iW9ptmh0BRkpMAf4FO2hFSCT8khGFbg2qHNTF_3GGK51SVIcXzs34SLtDbJZMiVVeCjUgYNTEo5cf58fhh8bBQg6wsHyzjoOS6tSRSdVC0X7znzzpTMIuRrDr9D3_keBh7QvDZddHimB0vs8NTmYSPXJTUdVThNE5l_XLyMXWeAZ_zl9e_VfOySl60ITomu1vVwqszcSbniCs6K8iCxrZDKcpHoW4nrydWQfkw_ruYjCEXaOlphrEyj8tspilDy4nYjGkZCoMygVOx4jJDw1xQYOXmcEFXU4X3GII27wTEv4u8JUpqq_uNnKpRh7-fEjTOk02maxttzXB4iCLgKgatmeLs-HxKwUMBcSJ559CQ-zR_oAygBfObwogx6vJn4JjJDb7ilfLhlpxhUZIFiybp8cIZgh0MA_3moLu5Ro"

        // WHEN
        val jwsWrapper = JwsWrapper(token)

        // THEN
        assertEquals("SomeUsername", jwsWrapper.getUsername())

    }

    @Test
    fun `getPermissions is correct`(){
        // GIVEN
        val permissions = listOf(Permissions.ADMIN_SERVICE, Permissions.CHESS_SERVICE, Permissions.REGISTRY_SERVICE)
        val token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic3ViIjoiU29tZVVzZXJuYW1lIiwiZXhwIjoxNzMwNDAyMzc5LCJpYXQiOjE3MzAzNzM1NzksInVzZXJJZCI6IjBmMzdjMTkyLTRjNTgtNGQyZS1iM2U5LTljNDMyZTk4ZWQzMSIsInBlcm1pc3Npb25zIjpbIkNIRVNTX1NFUlZJQ0UiLCJSRUdJU1RSWV9TRVJWSUNFIiwiQURNSU5fU0VSVklDRSJdfQ.pPYVies5vOWkP96ZLs_M9p0WLe85ery0Vr875MRHHQ5PtjwGU6YWCGX26-i-S8OJ9I5-ava_K3N5kjFJ2dkklEz7GYXnqaL_-QZa14SALTz3vfkx8dTt1CvTzf8_ImEckdbOoiK7iOHnu_kriuLCvJ7G4ep9u0wtaFohXLImhA0deKsHdlFaSfr7nHOy20XPIru0BIGf_N4w8xUhct8bC4ZoQzdHjxo447h1IXwjLSo890chVBU_dHLLi53aCUA7EG4iW9ptmh0BRkpMAf4FO2hFSCT8khGFbg2qHNTF_3GGK51SVIcXzs34SLtDbJZMiVVeCjUgYNTEo5cf58fhh8bBQg6wsHyzjoOS6tSRSdVC0X7znzzpTMIuRrDr9D3_keBh7QvDZddHimB0vs8NTmYSPXJTUdVThNE5l_XLyMXWeAZ_zl9e_VfOySl60ITomu1vVwqszcSbniCs6K8iCxrZDKcpHoW4nrydWQfkw_ruYjCEXaOlphrEyj8tspilDy4nYjGkZCoMygVOx4jJDw1xQYOXmcEFXU4X3GII27wTEv4u8JUpqq_uNnKpRh7-fEjTOk02maxttzXB4iCLgKgatmeLs-HxKwUMBcSJ559CQ-zR_oAygBfObwogx6vJn4JjJDb7ilfLhlpxhUZIFiybp8cIZgh0MA_3moLu5Ro"

        // WHEN
        val jwsWrapper = JwsWrapper(token)

        // THEN
        assertTrue(permissions.containsAll(jwsWrapper.getPermissions()))
        assertTrue(jwsWrapper.getPermissions().containsAll(permissions))
    }

}