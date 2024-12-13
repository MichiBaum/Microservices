package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.apis.fide.FideApiService
import com.michibaum.chess_service.app.FileImportResult
import com.michibaum.chess_service.app.event.EventService
import com.michibaum.chess_service.domain.Person
import jakarta.validation.Valid
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*


@RestController
class PersonController(
    private val personService: PersonService,
    private val personConverter: PersonConverter,
    private val fideApiService: FideApiService,
    private val eventService: EventService,
) {

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @GetMapping(value = ["/api/persons"])
    fun persons(): ResponseEntity<List<PersonDto>> {
        val personDtos = personService.getAll()
            .map { person -> personConverter.convert(person) }
        return ResponseEntity.ok(personDtos)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PutMapping(value = ["/api/persons"])
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
    @PostMapping(value = ["/api/persons/search"]) // TODO PostMapping?
    fun findPerson(@Valid @RequestBody personSearchDto: PersonSearchDto): ResponseEntity<List<PersonDto>> {
        val persons = personService.findByIfNotEmpty(
            personSearchDto.firstname, personSearchDto.lastname
        ).map { person -> personConverter.convert(person) }


        return ResponseEntity(persons, HttpStatus.OK)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PostMapping(value = ["/api/fide/ratings"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun fideImport(@RequestPart("file") filePartMono: Mono<FilePart>): Mono<ResponseEntity<FileImportResult>> {
        val processFileContent: (InputStream) -> List<Person> = { stream ->
            fideApiService.getPersons(stream).let {
                personService.createAndUpdate(it)
            }
        }
        return filePartMono.flatMap { filePart ->
            processFilePart(filePart.content(), processFileContent)
        }.map { processedContent ->
            val importResult = FileImportResult("", true)
            ResponseEntity(importResult, HttpStatus.OK)
        }.doOnError { error ->
            println("Error occurred: ${error.message}")
        }.onErrorResume {
            val importResult = FileImportResult("", false)
            Mono.just(ResponseEntity(importResult, HttpStatus.INTERNAL_SERVER_ERROR))
        }
    }

    private fun <T> processFilePart(content: Flux<DataBuffer>, processFileContent: (InputStream) -> T): Mono<T> {
        return DataBufferUtils.join(content).map { dataBuffer ->
            val byteArray = ByteArray(dataBuffer.readableByteCount()).apply {
                dataBuffer.read(this)
            }
            DataBufferUtils.release(dataBuffer) // Release the buffer after use
            val inputStream = ByteArrayInputStream(byteArray)
            processFileContent(inputStream)
        }
    }


}