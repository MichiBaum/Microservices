package com.michibaum.admin_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.Banner
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(basePackages = ["com.michibaum.authentication_library"])
class AdminServiceApplication

fun main(args: Array<String>) {
    runApplication<AdminServiceApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}