package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameRepository: JpaRepository<Game, UUID> {
    fun existsByPlatformAndExternalGameId(platform: ChessPlatform, externalGameId: String): Boolean
    fun findByEvent(event: Event): List<Game>

    @Query("SELECT g FROM Game g WHERE (g.whitePlayer.account.id = :id1 AND g.blackPlayer.account.id = :id2) OR (g.whitePlayer.account.id = :id2 AND g.blackPlayer.account.id = :id1)")
    fun findByAccounts(id1: UUID, id2: UUID): List<Game>

    @Query("SELECT g FROM Game g WHERE g.whitePlayer.account.id = :id OR g.blackPlayer.account.id = :id")
    fun findByAccount(id: UUID): List<Game>

    @Modifying
    @Query("update Game g set g.event = null where g.event = :event")
    fun nullifyEvent(event: Event)

}