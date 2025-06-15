package com.michibaum.alexandria_service.app.note

import com.michibaum.alexandria_service.database.note.Note
import com.michibaum.alexandria_service.database.note.NoteRepository
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val noteRepository: NoteRepository,
) {
    fun findAllByUserId(userId: String): List<Note> {
        return noteRepository.findAllByBelongsTo(userId)
    }


}