package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.AccountRepository
import com.michibaum.chess_service.domain.AccountProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class AccountControllerSearchIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun setUp() {
        accountRepository.deleteAll()
    }

    @Test
    fun `should search accounts by username`() {
        // GIVEN
        val username = "SearchableUsername"
        val account = AccountProvider.account(username = username)
        accountRepository.save(account)

        // WHEN
        mockMvc.perform(
            get("/api/accounts/search")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .param("accountName", username)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.[0].username").value(username)
        )
    }

    @Test
    fun `should search accounts by username and explicit local location`() {
        // GIVEN
        val username = "AnotherUsername"
        val account = AccountProvider.account(username = username)
        accountRepository.save(account)

        // WHEN
        mockMvc.perform(
            get("/api/accounts/search")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .param("accountName", username)
                .param("searchLocation", SearchLocation.LOCAL.name)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.[0].username").value(username)
        )
    }
}
