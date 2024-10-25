package com.michibaum.chess_service.app

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class PersonController(
    private val personService: PersonService,
    private val personConverter: PersonConverter
) {

    @PostMapping(value = ["/api/persons"])
    fun createPerson(@RequestBody personDto: CreatePersonDto): ResponseEntity<PersonDto> {
        val convertedPerson = personConverter.convert(personDto)
        val savedPerson = personService.savePerson(convertedPerson)
        val responsePersonDto = personConverter.convert(savedPerson)
        return ResponseEntity(responsePersonDto, HttpStatus.CREATED)
    }

    @Transactional
    @PostMapping(value = ["/api/persons/search"])
    fun findPerson(@Valid @RequestBody personSearchDto: PersonSearchDto): ResponseEntity<List<PersonDto>> {
        val persons = personService.findPersonsByFirstnameAndLastname(
            personSearchDto.firstname, personSearchDto.lastname
        ).map { person -> personConverter.convert(person) }


        return ResponseEntity(persons, HttpStatus.OK)
    }

}