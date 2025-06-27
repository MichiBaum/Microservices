package com.michibaum.alexandria_service.app.note

import com.michibaum.alexandria_service.database.note.Note
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class NoteConverter {
    fun convert(it: Note): NoteDto {
        return NoteDto(
            id = it.idOrThrow(),
            title = it.title,
            text = it.text,
            encrypted = it.encryped
        )
    }

    fun convertToEntity(dto: NoteRequestDto, id: UUID?, userId: String): Note {
        return Note(
            title = dto.title,
            text = dto.text,
            encryped = dto.encrypted,
            belongsTo = userId,
            id = id
        )
    }
}
