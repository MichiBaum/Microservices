package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository: JpaRepository<Account, UUID>, JpaSpecificationExecutor<Account> {
    @EntityGraph(value = "with-person")
    fun findByPlatformAndPlatformIdAndUsername(chessPlatform: ChessPlatform, id: String, username: String): Account?

    @EntityGraph(value = "with-person")
    fun findByPlatformAndPlatformId(platform: ChessPlatform, platformId: String): Account?

    @Query("""
        SELECT a FROM Account a
            JOIN FETCH a.person p
        WHERE p.id = :personId
    """)
    fun findAllByPerson(personId: UUID): List<Account>

}