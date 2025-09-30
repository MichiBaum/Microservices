package com.michibaum.alexandria_service.app.note

data class NoteRequestDto(
    val title: String,
    val content: String,
    val encrypted: Boolean
)