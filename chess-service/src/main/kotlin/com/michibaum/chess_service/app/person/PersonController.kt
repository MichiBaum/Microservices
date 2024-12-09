package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.apis.fide.FideApiService
import com.michibaum.chess_service.app.FileImportResult
import com.michibaum.chess_service.domain.Person
import jakarta.validation.Valid
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.io.InputStream


@RestController
class PersonController(
    private val personService: PersonService,
    private val personConverter: PersonConverter,
    private val fideApiService: FideApiService,
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

    @Transactional
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