package com.michibaum.chess_service

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import java.io.InputStream
import java.nio.charset.StandardCharsets

fun lichessMockserverJson(filename: String): String {
    val resource: Resource = ClassPathResource("mockserver/lichess/$filename")
    val inputStream: InputStream = resource.inputStream
    return String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
}

fun chesscomMockserverJson(filename: String): String {
    val resource: Resource = ClassPathResource("mockserver/chesscom/$filename")
    val inputStream: InputStream = resource.inputStream
    return String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
}