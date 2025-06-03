package com.michibaum.fitness_service.api.sleep

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class SleepController(
    private val sleepService: SleepService,
    private val sleepConverter: SleepConverter
) {

    @GetMapping(value = ["/api/sleeps"])
    fun getSleep(principal: JwtAuthentication): List<SleepDto> {
        return sleepService.getByUser(principal.getUserId())
            .map { sleepConverter.toDto(it) }
    }
    
    @GetMapping(value = ["/api/sleeps/{id}/stages"])
    fun getSleepStages(@PathVariable id: String): ResponseEntity<List<SleepStageDto>> {
        return try {
            val sleepId = UUID.fromString(id)
            val dtos = sleepService.getStagesBySleep(sleepId)
                .map {sleepConverter.toDto(it)}
            ResponseEntity.ok(dtos)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

}