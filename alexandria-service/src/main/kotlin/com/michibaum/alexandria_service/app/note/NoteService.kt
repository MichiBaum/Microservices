package com.michibaum.alexandria_service.app.note

import com.michibaum.alexandria_service.app.TextSanitizer
import com.michibaum.alexandria_service.database.note.Note
import com.michibaum.alexandria_service.database.note.NoteRepository
import com.michibaum.authentication_library.ForbiddenException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val textSanitizer: TextSanitizer
) {
    fun findAllByUserId(userId: String): List<Note> {
        return noteRepository.findAllByBelongsTo(userId)
    }

    fun createNote(note: Note): Note {
        val sanitized = textSanitizer.sanitize(note.content)
        val sanitizedNote = note.copy(content = sanitized)
        return noteRepository.save(sanitizedNote)
    }

    fun updateNote(note: Note, userId: String): Note {
        val sanitized = textSanitizer.sanitize(note.content)
        val sanitizedNote = note.copy(content = sanitized)
        
        val existingNote = noteRepository.findById(sanitizedNote.idOrThrow())
            .orElseThrow { NoSuchElementException("Note not found") }

        if (existingNote.belongsTo != userId) {
            throw ForbiddenException("User does not have permission to update this note")
        }

        return noteRepository.save(sanitizedNote)
    }

    fun findById(id: UUID): Note {
        return noteRepository.findById(id)
            .orElseThrow { NoSuchElementException("Note not found") }
    }
}
