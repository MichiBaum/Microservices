package com.michibaum.chess_service.app

import com.michibaum.chess_service.domain.Account
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    val accountService: AccountService,
    val accountConverter: AccountConverter
) {

    @GetMapping("/api/accounts")
    fun findAccount(@RequestParam username: String): List<AccountDto> {
        return accountService.getAccounts(username)
            .map { account: Account -> accountConverter.convert(account) }
    }

}