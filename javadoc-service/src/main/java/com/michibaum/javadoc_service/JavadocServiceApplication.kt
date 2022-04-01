package com.michibaum.javadoc_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
class JavadocServiceApplication

fun main(args: Array<String>) {
    runApplication<JavadocServiceApplication>(*args)
}