package com.michibaum.alexandria_service.app.note

import com.michibaum.alexandria_service.database.note.Note
import org.springframework.stereotype.Component

@Component
class NoteConverter {
    fun convert(it: Note): NoteDto {
        return NoteDto(
            it.idOrThrow(),
            text = it.text,
            encrypted = it.encryped
        )
    }
}