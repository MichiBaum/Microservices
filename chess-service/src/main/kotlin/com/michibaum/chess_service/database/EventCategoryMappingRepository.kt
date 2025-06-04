package com.michibaum.chess_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface EventCategoryMappingRepository: JpaRepository<EventCategoryMapping, EventCategoryMappingId> {
    
    @Query("""
        SELECT ecm.event as event, ecm.category as category 
        FROM EventCategoryMapping ecm 
        WHERE ecm.event IN :events
    """
    )
    fun findByEvents(events: List<Event>): Set<EventCategoryMappingProjection>

    @Query("""
        SELECT ecm.event as event, ecm.category as category 
        FROM EventCategoryMapping ecm 
        WHERE ecm.event = :event
    """
    )
    fun findByEvents(event: Event): Set<EventCategoryMappingProjection>
    
}