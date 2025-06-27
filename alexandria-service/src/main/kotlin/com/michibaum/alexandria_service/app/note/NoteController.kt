package com.michibaum.alexandria_service.app.note

import com.michibaum.authentication_library.ForbiddenException
import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class NoteController(
    private val noteService: NoteService,
    private val noteConverter: NoteConverter
) {

    @GetMapping("/api/notes")
    fun getNotes(principal: JwtAuthentication): List<NoteDto> {
        return noteService.findAllByUserId(principal.getUserId())
            .map { noteConverter.convert(it)}
    }

    @PostMapping("/api/notes")
    @ResponseStatus(HttpStatus.CREATED)
    fun createNote(@RequestBody noteRequest: NoteRequestDto, principal: JwtAuthentication): NoteDto {
        val note = noteConverter.convertToEntity(noteRequest, null, principal.getUserId())
        val savedNote = noteService.createNote(note)
        return noteConverter.convert(savedNote)
    }

    @PutMapping("/api/notes/{id}")
    fun updateNote(
        @PathVariable id: UUID,
        @RequestBody noteRequest: NoteRequestDto,
        principal: JwtAuthentication
    ): ResponseEntity<NoteDto> {
        try {
            val note = noteConverter.convertToEntity(noteRequest, id, principal.getUserId())
            val updatedNote = noteService.updateNote(note, principal.getUserId())
            return ResponseEntity.ok(noteConverter.convert(updatedNote))
        } catch (e: ForbiddenException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } catch (e: NoSuchElementException) {
            return ResponseEntity.notFound().build()
        } catch (e: IllegalAccessException) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }
}
