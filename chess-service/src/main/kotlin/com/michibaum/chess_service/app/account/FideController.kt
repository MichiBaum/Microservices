package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.apis.fide.FideApiService
import com.michibaum.chess_service.app.FileImportResult
import com.michibaum.chess_service.app.event.EventService
import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.ChessPlatform
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.io.InputStream

@RestController
class FideController(
    private val fideApiService: FideApiService,
    private val accountService: AccountService
) {


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    @PostMapping(value = ["/api/fide/ratings"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun fideImport(@RequestPart("file") file: MultipartFile): ResponseEntity<FileImportResult> { // TODO request doesnt work. Returns 413 but probably from gateway. Either way dont want to upload file. there should be a better way
        val processFn: (InputStream) -> List<Account> = { stream ->
            fideApiService.getAccounts(stream).let {
                val updated = accountService.createOrUpdate(ChessPlatform.FIDE, it)
                updated // TODO needs to be saved
            }
        }
        val x = file.inputStream.use { inputStream ->
            inputStream.buffered().use { bufferedInputStream ->
                processFn(bufferedInputStream)
            }
        }

        return ResponseEntity.ok(FileImportResult("To Import = ${x.size}", true))
    }

    private fun <T> processFilePart(content: Flux<DataBuffer>, processFn: (InputStream) -> T): Mono<T> {
        return DataBufferUtils.join(content).map { dataBuffer ->
            val byteArray = ByteArray(dataBuffer.readableByteCount()).apply {
                dataBuffer.read(this)
            }
            DataBufferUtils.release(dataBuffer) // Release the buffer after use
            val inputStream = ByteArrayInputStream(byteArray)
            processFn(inputStream)
        }
    }

}