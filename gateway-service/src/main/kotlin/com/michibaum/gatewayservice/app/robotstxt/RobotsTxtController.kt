package com.michibaum.gatewayservice.app.robotstxt

import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.annotation.Observed
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerResponse

@Component
class RobotsTxtController(
    private val robotsTxtService: RobotsTxtService,
    private val observationRegistry: ObservationRegistry
) {

    @Observed(name = "robots-txt-controller-get")
    fun robotsTxt(): HandlerFunction<ServerResponse> {
        return HandlerFunction { request ->
            val host = request.headers().firstHeader("Host")
            observationRegistry.currentObservation?.highCardinalityKeyValue("host", host ?: "unknown")
            val content = robotsTxtService.generateWith(host)
            ServerResponse.ok()
                .header("Content-Type", "text/plain")
                .body(content)
        }
    }

}