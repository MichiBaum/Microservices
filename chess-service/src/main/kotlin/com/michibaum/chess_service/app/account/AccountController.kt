package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.domain.Account
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AccountController(
    private val accountService: AccountService,
    private val accountConverter: AccountConverter
) {

    @GetMapping("/api/accounts/search/{accountName}")
    fun searchAccount(@PathVariable accountName: String, @RequestParam(required = false) local: Boolean = true): List<AccountDto> {
        return accountService.getAccounts(accountName, local)
            .map { account: Account -> accountConverter.convert(account) }
    }

    @GetMapping("/api/accounts/{id}")
    fun getAccount(@PathVariable id: String): ResponseEntity<AccountDto> {
        return try {
            val uuid = UUID.fromString(id)
            val account = accountService.findById(uuid) ?: return ResponseEntity.notFound().build()
            val dto = accountConverter.convert(account)
            ResponseEntity.ok(dto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }



}