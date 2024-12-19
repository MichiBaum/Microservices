package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.ChessPlatform
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository: JpaRepository<Account, UUID> {
    fun findByPlatformAndPlatformIdAndUsername(chessPlatform: ChessPlatform, id: String, username: String): Account?
    fun existsByPlatformIdAndUsername(id: String, username: String): Boolean
    fun findByUsernameContainingIgnoreCase(username: String): List<Account>

}