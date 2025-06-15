package com.michibaum.alexandria_service.app.note

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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

}