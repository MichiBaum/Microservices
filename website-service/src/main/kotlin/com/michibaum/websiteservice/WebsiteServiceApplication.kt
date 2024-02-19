package com.michibaum.websiteservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebsiteServiceApplication

fun main(args: Array<String>) {
    runApplication<WebsiteServiceApplication>(*args)
}
