package com.michibaum.authentication_library.web_logger

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse

fun ServerHttpRequest.toLoggableRequest(): Request {
    return object: Request {
        override fun getIpAddress(): String {
            return this@toLoggableRequest.remoteAddress?.address?.hostAddress ?: "unknown"
        }
    }
}

fun ServerHttpResponse.toLoggableResponse(): Response {
    return object: Response {
        
    }
}