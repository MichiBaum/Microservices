package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PopularOpeningRepository : JpaRepository<PopularOpening, UUID> {

    @Query("SELECT po FROM PopularOpening po left join fetch po.opening o ORDER BY po.ranking ASC")
    fun findAllByOrderByRankingAsc(): List<PopularOpening>

}