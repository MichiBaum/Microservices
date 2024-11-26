package com.michibaum.chess_service.domain

import java.time.LocalDateTime

class EventProvider {
    
    companion object {
        
        fun event(participants: Set<Person> = emptySet()): Event =
            Event(
                title = "2024 FIDE World Championship",
                url = "https://www.chess.com/events/2024-fide-chess-world-championship",
                embedUrl = "https://www.chess.com/events/embed/2024-fide-chess-world-championship",
                dateFrom = LocalDateTime.of(2024, 11, 25, 0, 0),
                dateTo = LocalDateTime.of(2024, 12, 13, 23, 59),
                participants = participants
            )

    }
    
}