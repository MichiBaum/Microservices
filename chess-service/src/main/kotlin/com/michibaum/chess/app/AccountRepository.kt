package com.michibaum.chess.app

import com.michibaum.chess.domain.Account
import com.michibaum.chess.domain.ChessPlatform
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository: JpaRepository<Account, UUID> {
    fun findByPlatformAndAccIdAndUsername(chessPlatform: ChessPlatform, id: String, username: String): Account?

}