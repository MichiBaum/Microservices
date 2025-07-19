package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository: JpaRepository<Account, UUID> {
    fun findByPlatformAndPlatformIdAndUsername(chessPlatform: ChessPlatform, id: String, username: String): Account?
    fun existsByPlatformIdAndUsername(id: String, username: String): Boolean
    fun findByUsernameContainingIgnoreCase(username: String): List<Account>
    fun findAllByPlatformAndPlatformIdIn(platform: ChessPlatform, platformIds: List<String>): List<Account>
    fun findByPlatformAndPlatformId(platform: ChessPlatform, platformId: String): Account?

}