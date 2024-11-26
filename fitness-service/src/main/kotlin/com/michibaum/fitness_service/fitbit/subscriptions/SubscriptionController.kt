package com.michibaum.fitness_service.fitbit.subscriptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SubscriptionController {

    @PostMapping("/api/fitbit/notifications", consumes = ["application/json"], produces = ["application/json"])
    fun notification(@RequestBody notifications: List<NotificationDto>): ResponseEntity<Unit> {
        // https://dev.fitbit.com/build/reference/web-api/developer-guide/best-practices/#Subscriber-Security
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @GetMapping("/api/fitbit/notifications", consumes = ["application/json"], produces = ["application/json"])
    fun subscriber(@RequestParam("verify") verifyCode: String){

        // correct verifyCode -> ResponseEntity.status(HttpStatus.NO_CONTENT).build()

        // incorrect verifyCode -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()

    }

}