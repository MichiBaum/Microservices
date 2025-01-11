package com.michibaum.chess_service.app.opening

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class OpeningController(
    private val openingService: OpeningService,
    private val openingConverter: OpeningConverter
) {

    @PostMapping("/api/openings")
    fun createOpening(@RequestBody dto: OpeningDto): ResponseEntity<OpeningResponseDto> {
        val opening = openingService.createOpening(dto)
        val response = openingConverter.toDto(opening)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/api/openings/{id}")
    fun updateOpening(@PathVariable id: UUID, @RequestBody dto: OpeningDto): ResponseEntity<OpeningResponseDto> {
        val opening = openingService.updateOpening(id, dto)
        val response = openingConverter.toDto(opening)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/api/openings/{id}/moves")
    fun getAllMovesForOpening(@PathVariable id: UUID): ResponseEntity<OpeningMoveDto> {
        // Fetch the opening and its lastMove
        val opening = openingService.getOpeningById(id)

        // Convert to a hierarchical DTO using the `lastMove`
        val moveHierarchy = openingConverter.buildMoveHierarchy(opening.lastMove)

        // Return the hierarchical DTO as the response
        return ResponseEntity.ok(moveHierarchy)
    }
}