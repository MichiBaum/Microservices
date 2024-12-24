package com.michibaum.chess_service.app.person

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
class PersonController(
    private val personService: PersonService,
    private val personConverter: PersonConverter
) {

    @PublicEndpoint
    @GetMapping(value = ["/api/persons"])
    fun persons(): ResponseEntity<List<PersonDto>> {
        val personDtos = personService.getAllEagerAccounts()
            .map { person -> personConverter.convert(person) }
        return ResponseEntity.ok(personDtos)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @GetMapping(value = ["/api/persons/{id}"])
    fun person(@PathVariable id: String): ResponseEntity<PersonDto> {
        return try {
            val uuid = UUID.fromString(id)
            val person = personService.find(uuid)?:
                return ResponseEntity.notFound().build()
            val personDto = personConverter.convert(person)
            ResponseEntity.ok(personDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PutMapping(value = ["/api/persons"]) // TODO change to POST
    fun createPerson(@Valid @RequestBody personDto: WritePersonDto): ResponseEntity<PersonDto> {
        return try {
            val newPerson = personService.create(personDto)
            val responsePersonDto = personConverter.convert(newPerson)
            ResponseEntity(responsePersonDto, HttpStatus.CREATED)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PutMapping(value = ["/api/persons/{id}"])
    fun updatePerson(@PathVariable id: String, @Valid @RequestBody personDto: WritePersonDto): ResponseEntity<PersonDto> {
        return try {
            val uuid = UUID.fromString(id)
            val person = personService.find(uuid)?:
                return ResponseEntity.notFound().build()
            val newPerson = personService.update(person, personDto)
            val responsePersonDto = personConverter.convert(newPerson)
            return ResponseEntity.ok(responsePersonDto)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @PostMapping(value = ["/api/persons/search"]) // TODO PostMapping? GET Mapping with only Dto. No @... needed (other than @Valid)
    fun findPerson(@Valid @RequestBody personSearchDto: PersonSearchDto): ResponseEntity<List<PersonDto>> {
        val persons = personService.findByIfNotEmpty(
            personSearchDto.firstname, personSearchDto.lastname
        ).map { person -> personConverter.convert(person) }


        return ResponseEntity(persons, HttpStatus.OK)
    }

}