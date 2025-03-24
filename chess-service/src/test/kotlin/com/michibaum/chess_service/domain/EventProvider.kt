package com.michibaum.chess_service.domain

import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.Event
import com.michibaum.chess_service.database.Person
import java.time.LocalDate

class EventProvider {
    
    companion object {
        
        fun event(
            dateFrom: LocalDate = LocalDate.now().minusWeeks(2),
            dateTo: LocalDate = LocalDate.now().minusWeeks(1),
            participants: MutableSet<Person> = mutableSetOf()
        ): Event =
            Event(
                title = "2024 FIDE World Championship",
                location = "Zürich",
                url = "https://www.chess.com/events/2024-fide-chess-world-championship",
                embedUrl = "https://www.chess.com/events/embed/2024-fide-chess-world-championship",
                dateFrom = dateFrom,
                dateTo = dateTo,
                participants = participants,
                internalComment = "",
                platform = ChessPlatform.FIDE
            )

    }
    
}