package com.michibaum.gatewayservice.app.robotstxt

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class RobotsTxtController(
    private val robotsTxtService: RobotsTxtService
) {

    @GetMapping("/robots.txt", produces = ["text/plain"])
    fun robotsTxt(@RequestHeader("Host") host: String?): String {
        return robotsTxtService.generateWith(host)
    }

}