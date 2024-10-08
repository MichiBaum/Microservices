package com.michibaum.chess.app

import com.michibaum.chess.anyIterable
import com.michibaum.chess.apis.ApiService
import com.michibaum.chess.apis.Success
import com.michibaum.chess.apis.dtos.AccountDto
import com.michibaum.chess.domain.Account
import com.michibaum.chess.domain.ChessPlatform
import com.michibaum.chess.domain.Player
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class AccountServiceUT {

    private val apiService: ApiService = mockk()
    private val accountRepository: AccountRepository = mockk()
    private val accountService: AccountService = AccountService(apiService, accountRepository)

    @Test
    fun `successfully found account` (){
        // GIVEN
        val username = "someUsername"
        val apiAccount = AccountDto(
            id = "someId",
            url = "someUrl",
            username = username,
            name = "Some Name",
            platform = ChessPlatform.CHESSCOM,
            createdAt = Date()
        )
        every { apiService.findAccount(username) } returns listOf(Success(apiAccount))
        every { accountRepository.saveAll(anyIterable()) } returnsArgument 0

        // WHEN
        val result = accountService.getAccounts(username)

        // THEN
        assertEquals(1, result.size)
        assertEquals(apiAccount.id, result[0].accId)
        assertEquals(apiAccount.username, result[0].username)

    }

    @Test
    fun `successfully found multiple accounts` (){
        // GIVEN
        val username = "someUsername"
        val apiAccount1 = AccountDto(
            id = "someId1",
            url = "someUrl1",
            username = username,
            name = "Some Name1",
            platform = ChessPlatform.CHESSCOM,
            createdAt = Date()
        )
        val apiAccount2 = AccountDto(
            id = "someId2",
            url = "someUrl2",
            username = username,
            name = "Some Name2",
            platform = ChessPlatform.LICHESS,
            createdAt = Date()
        )
        every { apiService.findAccount(username) } returns listOf(Success(apiAccount1), Success(apiAccount2))
        every { accountRepository.saveAll(anyIterable()) } returnsArgument 0

        // WHEN
        val result = accountService.getAccounts(username)

        // THEN
        assertEquals(2, result.size)
    }

}