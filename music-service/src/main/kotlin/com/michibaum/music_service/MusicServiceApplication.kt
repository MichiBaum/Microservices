package com.michibaum.music_service

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
class MusicServiceApplication

fun main(args: Array<String>) {
    runApplication<MusicServiceApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}