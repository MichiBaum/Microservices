package com.michibaum.gatewayservice.app.sitemapxml

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping

@Component
class DataLocationsFetcher(
    private val chessClient: ChessClient
) {

    fun fetch(dataLocation: DataLocation): List<String>{
        return when (dataLocation) {
            DataLocation.CHESS_EVENTS -> chessClient.events().map { it.id }
            DataLocation.CHESS_OPENINGS -> chessClient.openings().map { it.id }
        }
    }

}

@FeignClient(value = "chess-service")
interface ChessClient{
    @GetMapping(value = ["/api/events"])
    fun events(): List<EventDto>
    @GetMapping(value = ["/api/openings"])
    fun openings(): List<OpeningDto>
}

data class EventDto(val id: String)
data class OpeningDto(val id: String)