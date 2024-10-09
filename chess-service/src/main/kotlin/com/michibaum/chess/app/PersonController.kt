package com.michibaum.chess.app

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PersonController(
    private val personService: PersonService,
    private val personConverter: PersonConverter
) {

    @PostMapping(value = ["/api/persons"])
    fun createPerson(@RequestBody personDto: PersonDto): ResponseEntity<PersonDto> {
        val convertedPerson = personConverter.convert(personDto)
        val savedPerson = personService.savePerson(convertedPerson)
        val responsePersonDto = personConverter.convert(savedPerson)
        return ResponseEntity(responsePersonDto, HttpStatus.CREATED)
    }

}