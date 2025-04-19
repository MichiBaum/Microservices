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
        val openings = openingService.getAllEagerLastMove(false)
        val dtos = openings.map { opening -> openingConverter.toDto(opening) }
        return ResponseEntity.ok(dtos)
    }

    @PublicEndpoint
    @GetMapping("/api/openings/starting")
    fun getAllStartingOpenings(): ResponseEntity<List<OpeningResponseDto>> {
        val openings = openingService.findOpeningByParentMoveAndDeleted(null, false)
        val dtos = openings.map { opening -> openingConverter.toDto(opening) }
        return ResponseEntity.ok(dtos)
    }

    @PublicEndpoint
    @GetMapping("/api/openings/popular")
    fun getAllPopularOpenings(): ResponseEntity<List<PopularOpeningResponseDto>> {
        val popularOpenings = openingService.getPopularOpenings()
        val dtos = popularOpenings.map { opening -> openingConverter.toDto(opening) }
        return ResponseEntity.ok(dtos)
    }

    @PostMapping("/api/openings")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun createOpening(@RequestBody dto: OpeningDto): ResponseEntity<OpeningResponseDto> {
        val opening = openingService.createOpening(dto)
        val response = openingConverter.toDto(opening)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/api/openings/{id}") // TODO change to PUT
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

    @PostMapping("/api/openings/moves/{id}") // TODO change to PUT
    fun updateMove(@RequestBody dto: WriteOpeningMoveDto): ResponseEntity<SimpleOpeningMoveDto> {
        return try {
            val uuid = UUID.fromString(dto.id)
            val move = openingService.findMoveBy(uuid) ?:
                return ResponseEntity.notFound().build()
            val updated = openingService.updateMove(dto, move)
            val newDto = openingConverter.convert(updated)
            return ResponseEntity.ok(newDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @DeleteMapping("/api/openings/{id}")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun deleteOpening(@PathVariable id: String): ResponseEntity<OpeningResponseDto> {
        return try {
            val uuid = UUID.fromString(id)
            val opening = openingService.getOpeningByIdEagerLastMove(uuid) ?:
                return ResponseEntity.notFound().build()
            val updated = openingService.deleteOpening(opening)
            val dto = openingConverter.toDto(updated)
            return ResponseEntity.ok(dto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @DeleteMapping("/api/openings/moves/{id}")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun deleteMove(@PathVariable id: String): ResponseEntity<SimpleOpeningMoveDto> {
        return try {
            val uuid = UUID.fromString(id)
            val move = openingService.findMoveBy(uuid) ?:
                return ResponseEntity.notFound().build()
            val updated = openingService.deleteMove(move)
            val newDto = openingConverter.convert(updated)
            return ResponseEntity.ok(newDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PublicEndpoint
    @GetMapping("/api/openings/{id}/children")
    fun getAllChildrenFromOpening(@PathVariable id: String, @RequestParam maxDepth: Int = 10): ResponseEntity<OpeningMoveDto> {
        return try {
            val uuid = UUID.fromString(id)
            val opening = openingService.getOpeningByIdEagerLastMove(uuid) ?:
                return ResponseEntity.notFound().build()
            val engines = engineService.getAllChessEngines()
            val lastMove = opening.lastMove
            val moves = openingService.findMoveChildren(lastMove, maxDepth)
            val moveHierarchy = openingConverter.buildMoveHierarchy(moves, engines, lastMove.id)
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
            val opening = openingService.getOpeningByIdEagerLastMove(uuid) ?:
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
    @GetMapping("/api/openings/moves/{id}/evaluations")
    fun getEvaluation(@PathVariable id: String): ResponseEntity<List<OpeningMoveEvaluationDto>>{
        return try {
            val uuid = UUID.fromString(id)
            val move = openingService.findMoveEagerEvaluations(uuid) ?:
                return ResponseEntity.notFound().build()
            val evaluations = move.moveEvaluations
            val dtos = evaluations.map { openingConverter.toDto(it) }
            return ResponseEntity.ok(dtos)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @DeleteMapping("/api/openings/evaluations/{id}")
    fun deleteEvaluation(@PathVariable id: String): ResponseEntity<Boolean>{
        return try {
            val uuid = UUID.fromString(id)
            openingService.deleteEvaluation(uuid)
            return ResponseEntity.ok(true)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/api/openings/moves/{id}/evaluations")
    fun createEvaluation(@PathVariable id: String, @RequestBody dto: WriteOpeningEvaluationDto): ResponseEntity<OpeningMoveEvaluationDto> {
        return try {
            val uuid = UUID.fromString(id)
            val openingMove = openingService.findMoveEagerEvaluations(uuid) ?:
                return ResponseEntity.notFound().build()
            val engineId = UUID.fromString(dto.engineId)
            val engine = engineService.findById(engineId) ?:
                return ResponseEntity.notFound().build()
            val newEvaluation = openingService.createEvaluation(dto,openingMove, engine)
            val newDto = openingConverter.toDto(newEvaluation)
            return ResponseEntity.ok(newDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PutMapping("/api/openings/moves/{moveId}/evaluations/{id}")
    fun updateEvaluation(@PathVariable moveId: String, @PathVariable id: String, @RequestBody dto: WriteOpeningEvaluationDto): ResponseEntity<OpeningMoveEvaluationDto>{
        return try {
            val evaluationId = UUID.fromString(id)
            val evolution = openingService.findEvolution(evaluationId) ?:
                return ResponseEntity.notFound().build()
            val engineId = UUID.fromString(dto.engineId)
            val engine = engineService.findById(engineId) ?:
                return ResponseEntity.notFound().build()
            val newEvaluation = openingService.updateEvaluation(evolution, engine, dto)
            val newDto = openingConverter.toDto(newEvaluation)
            return ResponseEntity.ok(newDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}