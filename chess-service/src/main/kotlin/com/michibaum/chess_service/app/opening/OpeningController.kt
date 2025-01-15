package com.michibaum.chess_service.app.opening

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class OpeningController(
    private val openingService: OpeningService,
    private val openingConverter: OpeningConverter
) {

    @PublicEndpoint
    @GetMapping("/api/openings")
    fun getAllOpenings(): ResponseEntity<List<OpeningResponseDto>> {
        val openings = openingService.getAll()
        val dtos = openings.map { opening -> openingConverter.toDto(opening) }
        return ResponseEntity.ok(dtos)
    }

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

    @PublicEndpoint
    @GetMapping("/api/openings/{id}/moves")
    fun getAllMovesForOpening2(@PathVariable id: String): ResponseEntity<OpeningMoveDto> {
        return try {
            val uuid = UUID.fromString(id)
            val opening = openingService.getOpeningById(uuid) ?:
            return ResponseEntity.notFound().build()
            val moves = openingService.findMoveHierarchy(opening.lastMove)
            val moveHierarchy = openingConverter.buildMoveHierarchy(moves)
            return ResponseEntity.ok(moveHierarchy)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }

    }
}