package com.michibaum.chess_service.apis.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(ChessConfigProperties::class)
class ApisConfig {

    @Bean
    fun chessConfigProperties(): ChessConfigProperties = ChessConfigProperties()

}