package com.michibaum.chess

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients
@EnableConfigurationProperties
class ChessServiceApplication

fun main(args: Array<String>) {
    runApplication<ChessServiceApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}
