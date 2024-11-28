package com.michibaum.chess_service.domain

import java.time.LocalDate
import java.time.LocalDateTime

class EventProvider {
    
    companion object {
        
        fun event(participants: Set<Person> = emptySet()): Event =
            Event(
                title = "2024 FIDE World Championship",
                url = "https://www.chess.com/events/2024-fide-chess-world-championship",
                embedUrl = "https://www.chess.com/events/embed/2024-fide-chess-world-championship",
                dateFrom = LocalDate.of(2024, 11, 25),
                dateTo = LocalDate.of(2024, 12, 13),
                participants = participants
            )

    }
    
}