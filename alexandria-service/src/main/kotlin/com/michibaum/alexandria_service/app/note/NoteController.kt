package com.michibaum.alexandria_service.app.note

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NoteController {

    @GetMapping("/api/notes")
    public fun getNotes(): List<Note> {}

}