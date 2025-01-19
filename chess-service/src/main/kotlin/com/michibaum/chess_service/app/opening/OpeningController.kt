package com.michibaum.chess_service.app.opening

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import com.michibaum.chess_service.app.chessengine.ChessEngineService
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class OpeningController(
    private val openingService: OpeningService,
    private val openingConverter: OpeningConverter,
    private val engineService: ChessEngineService
) {

    @PublicEndpoint
    @GetMapping("/api/openings")
    fun getAllOpenings(): ResponseEntity<List<OpeningResponseDto>> {
        val openings = openingService.getAll()
        val dtos = openings.map { opening -> openingConverter.toDto(opening) }
        return ResponseEntity.ok(dtos)
    }

    @PublicEndpoint
    @GetMapping("/api/openings/starting")
    fun getAllStartingMoves(): ResponseEntity<List<OpeningResponseDto>> {
        val moves = openingService.findMoveByParent(null)
        val openings = openingService.openingsByMoves(moves)
        val dtos = openings.map { opening -> openingConverter.toDto(opening) }
        return ResponseEntity.ok(dtos)
    }

    @PostMapping("/api/openings")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun createOpening(@RequestBody dto: OpeningDto): ResponseEntity<OpeningResponseDto> {
        val opening = openingService.createOpening(dto)
        val response = openingConverter.toDto(opening)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/api/openings/{id}")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun updateOpening(@PathVariable id: UUID, @RequestBody dto: OpeningDto): ResponseEntity<OpeningResponseDto> {
        val opening = openingService.updateOpening(id, dto)
        val response = openingConverter.toDto(opening)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/api/openings/moves")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun createMove(@RequestBody dto: WriteOpeningMoveDto): ResponseEntity<SimpleOpeningMoveDto> {
        return try {
            val uuid = UUID.fromString(dto.parentMoveId)
            val parentMove = openingService.findMoveBy(uuid) ?:
                return ResponseEntity.notFound().build()
            val created = openingService.createMove(dto, parentMove)
            val newDto = openingConverter.convert(created)
            return ResponseEntity.ok(newDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PublicEndpoint
    @GetMapping("/api/moves/{id}/children")
    fun getAllChildren(@PathVariable id: String): ResponseEntity<OpeningMoveDto> {
        return try {
            val uuid = UUID.fromString(id)
            val move = openingService.findMoveBy(uuid) ?:
                return ResponseEntity.notFound().build()
            val engines = engineService.getAllChessEngines()
            val moves = openingService.findMoveChildren(move)
            val moveHierarchy = openingConverter.buildMoveHierarchy(moves, engines, move.id)
            return ResponseEntity.ok(moveHierarchy)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PublicEndpoint
    @GetMapping("/api/openings/{id}/moves")
    fun getAllMovesForOpening(@PathVariable id: String): ResponseEntity<OpeningMoveDto> {
        return try {
            val uuid = UUID.fromString(id)
            val opening = openingService.getOpeningById(uuid) ?:
                return ResponseEntity.notFound().build()
            val engines = engineService.getAllChessEngines()
            val moves = openingService.findMoveHierarchy(opening.lastMove)
            val moveHierarchy = openingConverter.buildMoveHierarchy(moves, engines)
            return ResponseEntity.ok(moveHierarchy)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PublicEndpoint
    @GetMapping("/api/openings/{id}/children")
    fun getAllChildrenFromOpening(@PathVariable id: String): ResponseEntity<OpeningMoveDto> {
        return try {
            val uuid = UUID.fromString(id)
            val opening = openingService.getOpeningById(uuid) ?:
                return ResponseEntity.notFound().build()
            val engines = engineService.getAllChessEngines()
            val lastMove = opening.lastMove
            val moves = openingService.findMoveChildren(lastMove)
            val moveHierarchy = openingConverter.buildMoveHierarchy(moves, engines, lastMove.id)
            return ResponseEntity.ok(moveHierarchy)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}