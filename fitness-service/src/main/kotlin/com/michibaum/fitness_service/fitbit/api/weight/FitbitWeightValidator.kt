package com.michibaum.fitness_service.fitbit.api.weight

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Component
class FitbitWeightValidator {

    fun validate(startDate: String, endDate: String): ResponseEntity<Void>?{
        try {
            val startLocalDate = LocalDate.parse(startDate)
            val endLocalDate = LocalDate.parse(endDate)
            if (endLocalDate.isBefore(startLocalDate)) {
                return ResponseEntity.badRequest().build()
            }
            if(endLocalDate.until(startLocalDate).days > 31){
                return ResponseEntity.badRequest().build()
            }
        } catch (e: DateTimeParseException) {
            return ResponseEntity.badRequest().build()
        }
        return null
    }

}