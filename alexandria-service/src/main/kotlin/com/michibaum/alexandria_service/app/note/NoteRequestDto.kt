package com.michibaum.alexandria_service.app.note

data class NoteRequestDto(
    val title: String,
    val text: String,
    val encrypted: Boolean
)