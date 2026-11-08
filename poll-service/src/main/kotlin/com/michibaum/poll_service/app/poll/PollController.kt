package com.michibaum.poll_service.app.poll

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.poll_service.app.poll.dto.*
import com.michibaum.poll_service.database.PollStatus
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class PollController(
    private val pollService: PollService
) {

    private fun extractUserId(authentication: Authentication): String {
        return (authentication as? JwtAuthentication)?.getUserId() ?: authentication.name
    }

    @PostMapping(value = ["/api/v1/creator/polls", "/api/polls"])
    fun create(
        @Valid @RequestBody dto: CreatePollDto,
        authentication: Authentication
    ): ResponseEntity<PollDetailDto> {
        val creatorId = extractUserId(authentication)
        val created = pollService.create(dto, creatorId)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @GetMapping(value = ["/api/v1/creator/polls", "/api/polls"])
    fun getAll(
        @RequestParam(required = false) status: PollStatus?,
        @RequestParam(required = false) search: String?,
        @PageableDefault(page = 0, size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        authentication: Authentication
    ): ResponseEntity<Page<PollSummaryDto>> {
        val creatorId = extractUserId(authentication)
        val page = pollService.getAll(creatorId, status, search, pageable)
        return ResponseEntity.ok(page)
    }

    @GetMapping(value = ["/api/v1/creator/polls/{id}", "/api/polls/{id}"])
    fun get(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<PollDetailDto> {
        val creatorId = extractUserId(authentication)
        val poll = pollService.get(id, creatorId)
        return ResponseEntity.ok(poll)
    }

    @PutMapping(value = ["/api/v1/creator/polls/{id}", "/api/polls/{id}"])
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody dto: UpdatePollDto,
        authentication: Authentication
    ): ResponseEntity<PollDetailDto> {
        val creatorId = extractUserId(authentication)
        val updated = pollService.update(id, dto, creatorId)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping(value = ["/api/v1/creator/polls/{id}", "/api/polls/{id}"])
    fun delete(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        val creatorId = extractUserId(authentication)
        pollService.delete(id, creatorId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping(value = ["/api/v1/creator/polls/{id}/duplicate", "/api/polls/{id}/duplicate"])
    fun duplicate(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<PollDetailDto> {
        val creatorId = extractUserId(authentication)
        val duplicated = pollService.duplicate(id, creatorId)
        return ResponseEntity.status(HttpStatus.CREATED).body(duplicated)
    }
}
