package com.michibaum.alexandria_service.app.note

import java.util.UUID

data class NoteDto(
    val id: UUID,
    val text: String,
    val encrypted: Boolean
)
