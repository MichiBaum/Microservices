package com.michibaum.gatewayservice

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.michibaum.authentication_library.AuthenticationClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(properties = [
    "spring.cloud.openfeign.client.config.authentication-service.url=http://localhost:8899",
    "spring.cloud.openfeign.micrometer.enabled=false", // Disable micrometer for openfeign client because of exception if url set like above
    "eureka.client.enabled=false"
])
@AutoConfigureMockMvc
class AuthenticationClientIT {

    companion object {
        @RegisterExtension
        var wireMockServer: WireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8899))
            .build()
    }

    @Autowired
    lateinit var client: WebTestClient

    @Autowired
    lateinit var authenticationClient: AuthenticationClient

    @Test
    fun `test authenticationClient is created`() {
        // GIVEN
        val responseBody =
            "{\"algorithm\":\"RSA\",\"key\":\"MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAvjfO6AhWilSevVFDsdkOGRFiIfqBeOtuMM6FKu6GZk2tvd4eBXgt4hm5WjoPjUrCm2aTYIBN6+vs7yLoBrD/JybIX/XRyPNMOekytrnDwgtD1/SZdx1j8mWjDkZQi9bZUo0pZuLf9s7MmZiKRX3hFKuV/ltsyqDY3nKAVSEsb/iUHtCqUrluHKTgCA/7nBSBuNv64PbkGT2lFcWBu6FQi99NhBoFx9oNKdRFAL3O2RY87HxR4YKv5dzgeI9L8J8rTQLVHbaIP3bAfgh3TMen0MszXWcnW3ZK3er1wS9qYlSmYn0n/nRI8sc7EZTtfYbNR/3qg33e1J5+FTU62ySvWGQjSth74rf2zDn4qCQll99/WDtQCPyLCmb7aQx3KCaBtssxWp9RTs+LIBpPz518zudvQ7IUlwBqnkbi94+TvLpERlRkaVhEFoc8RWY1PlOzjevwUo2Cx2a0QxntLrfNMeI3HL+xMeOcwgn1yL88fEh3Nvmvjt53D9FSCkrAhvSpCyuu7Ny8bkFMBXT44x5r0Sz25HC+pNIHsde/PxxslHppUIamVTO6qDHkoya6RviJ0s9KbCJjJns/RZ67Ax9upeaho/Z3vfcFWjZIJEbG4BfExV/fugjZnjRR8rCxXGTxIGxrDgx6GOa04XiHX8b/EAR97+xzWugczvIxQCdpTPkCAwEAAQ==\"}\n"

        wireMockServer.stubFor(
            WireMock.get("/getAuthDetails")
                .willReturn(WireMock.okJson(responseBody))
        )

        // WHEN
        val response = authenticationClient.publicKey()

        // THEN
        Assertions.assertThat(response.algorithm).isNotNull()
        Assertions.assertThat(response.key).isNotNull()
    }

    @Test
    fun `getAuthDetails should not be found`() {
        client.get()
            .uri("/getAuthDetails")
            .exchange()
            .expectAll(
                {it.expectStatus().isNotFound}
            )

    }

}