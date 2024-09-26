package com.michibaum.gatewayservice

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.michibaum.authentication_library.AuthenticationClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import


@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationClientIT {

    @Autowired
    lateinit var authenticationClient: AuthenticationClient

    @TestConfiguration
    class TestConfig {
        @Bean
        fun serviceInstanceListSupplier(): ServiceInstanceListSupplier {
            return TestServiceInstanceListSupplier("authentication-service", IntArray(8899))
        }
    }

    @Test
    fun df(){
        // GIVEN
        val responseBody = "{\"algorithm\":\"RSA\",\"key\":\"MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAvjfO6AhWilSevVFDsdkOGRFiIfqBeOtuMM6FKu6GZk2tvd4eBXgt4hm5WjoPjUrCm2aTYIBN6+vs7yLoBrD/JybIX/XRyPNMOekytrnDwgtD1/SZdx1j8mWjDkZQi9bZUo0pZuLf9s7MmZiKRX3hFKuV/ltsyqDY3nKAVSEsb/iUHtCqUrluHKTgCA/7nBSBuNv64PbkGT2lFcWBu6FQi99NhBoFx9oNKdRFAL3O2RY87HxR4YKv5dzgeI9L8J8rTQLVHbaIP3bAfgh3TMen0MszXWcnW3ZK3er1wS9qYlSmYn0n/nRI8sc7EZTtfYbNR/3qg33e1J5+FTU62ySvWGQjSth74rf2zDn4qCQll99/WDtQCPyLCmb7aQx3KCaBtssxWp9RTs+LIBpPz518zudvQ7IUlwBqnkbi94+TvLpERlRkaVhEFoc8RWY1PlOzjevwUo2Cx2a0QxntLrfNMeI3HL+xMeOcwgn1yL88fEh3Nvmvjt53D9FSCkrAhvSpCyuu7Ny8bkFMBXT44x5r0Sz25HC+pNIHsde/PxxslHppUIamVTO6qDHkoya6RviJ0s9KbCJjJns/RZ67Ax9upeaho/Z3vfcFWjZIJEbG4BfExV/fugjZnjRR8rCxXGTxIGxrDgx6GOa04XiHX8b/EAR97+xzWugczvIxQCdpTPkCAwEAAQ==\"}\n"

        inventoryService.stubFor(
            WireMock.post("/getAuthDetails")
                .willReturn(WireMock.okJson(responseBody))
        )

        // WHEN
        var result = authenticationClient.publicKey()

        // THEN
        println(result)

    }

    companion object {
        @JvmStatic
        @RegisterExtension
        var inventoryService: WireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8899))
            .build()
    }

}