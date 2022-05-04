package com.michibaum.admin_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
@EnableScheduling
class AdminServiceApplication

fun main(args: Array<String>) {
    runApplication<AdminServiceApplication>(*args)
}