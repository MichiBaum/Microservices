package com.michibaum.admin_service

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
@EnableScheduling
class AdminServiceApplication

fun main(args: Array<String>) {
    runApplication<AdminServiceApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}