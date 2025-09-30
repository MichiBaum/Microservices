package com.michibaum.alexandria_service.app.note

import java.util.UUID

data class NoteDto(
    val id: UUID,
    val title: String,
    val content: String,
    val encrypted: Boolean
)
