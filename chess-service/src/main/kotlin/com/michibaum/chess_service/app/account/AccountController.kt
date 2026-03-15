package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class AccountController(
    private val accountService: AccountService,
    private val accountConverter: AccountConverter
) {

    @GetMapping("/api/accounts/search/{accountName}")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun searchAccount(@PathVariable accountName: String, @RequestParam(name = "search-location", required = false) searchLocation: SearchLocation = SearchLocation.LOCAL): List<GetAccountDto> {
        return accountService.getAccounts(accountName, searchLocation)
            .map { account: Account -> accountConverter.convert(account) }
    }

    @GetMapping("/api/accounts/{id}")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    fun getAccount(@PathVariable id: String): ResponseEntity<GetAccountDto> {
        return try {
            val uuid = UUID.fromString(id)
            val account = accountService.findByAccountId(uuid) ?: return ResponseEntity.notFound().build()
            val dto = accountConverter.convert(account)
            ResponseEntity.ok(dto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PostMapping("/api/accounts")
    fun createAccount(@Valid @RequestBody accountDto: WriteAccountDto): ResponseEntity<GetAccountDto> {
        return try {
            val account = accountService.create(accountDto)
            val newAccountDto = accountConverter.convert(account)
            ResponseEntity(newAccountDto, HttpStatus.CREATED)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PutMapping("/api/accounts/{id}")
    fun updateAccount(@PathVariable id: String, @Valid @RequestBody accountDto: WriteAccountDto): ResponseEntity<GetAccountDto> {
        return try {
            val uuid = UUID.fromString(id)
            val account = accountService.findByAccountId(uuid) ?: return ResponseEntity.notFound().build()
            val newAccount = accountService.update(account, accountDto)
            val newAccountDto = accountConverter.convert(newAccount)
            ResponseEntity.ok(newAccountDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @DeleteMapping("/api/accounts/{id}")
    fun deleteAccount(@PathVariable id: String): ResponseEntity<Void> {
        return try {
            val uuid = UUID.fromString(id)
            val account = accountService.findByAccountId(uuid) ?: return ResponseEntity.notFound().build()
            accountService.delete(account)
            ResponseEntity.noContent().build()
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}