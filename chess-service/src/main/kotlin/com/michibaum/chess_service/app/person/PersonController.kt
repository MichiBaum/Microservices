package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.apis.fide.FideImporter
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class PersonController(
    private val personService: PersonService,
    private val personConverter: PersonConverter,
    private val fideImporter: FideImporter
) {

    @Transactional
    @GetMapping(value = ["/api/persons"])
    fun persons(): ResponseEntity<List<PersonDto>> {
        val personDtos = personService.getAll()
            .map { person -> personConverter.convert(person) }
        return ResponseEntity.ok(personDtos)
    }

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
        val persons = personService.findByIfNotEmpty(
            personSearchDto.firstname, personSearchDto.lastname
        ).map { person -> personConverter.convert(person) }


        return ResponseEntity(persons, HttpStatus.OK)
    }

    @PostMapping(value = ["/api/fide/ratings"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_XML_VALUE])
    fun fideImport(@RequestPart("file") file: MultipartFile){
        val players = fideImporter.import(file.inputStream)
        val playersToImport = players.asSequence()
            .filter { it.rating < 2000 }
            .filter { it.fideid.isNotBlank() }
            .filter { it.name.isNotBlank() }
            .filter { it.country.isNotBlank() }
            .filter { it.name.contains(",") }
            .toList()
        // TODO Service find player by fideId >
        //  > exists -> update rating, country (not Name). If no fide otb account exists create one
        //  > not found -> create person with name, rating, country and gender. Also create an otb account
    }

}