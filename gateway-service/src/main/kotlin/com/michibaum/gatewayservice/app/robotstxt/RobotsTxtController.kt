package com.michibaum.gatewayservice.app.robotstxt

import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.HandlerFunction
import org.springframework.web.servlet.function.ServerResponse

@Component
class RobotsTxtController(
    private val robotsTxtService: RobotsTxtService
) {

    fun robotsTxt(): HandlerFunction<ServerResponse> {
        return HandlerFunction { request ->
            val host = request.headers().firstHeader("Host")
            val content = robotsTxtService.generateWith(host)
            ServerResponse.ok()
                .header("Content-Type", "text/plain")
                .body(content)
        }
    }

}