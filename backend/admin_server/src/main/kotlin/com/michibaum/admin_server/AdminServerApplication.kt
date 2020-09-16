package com.michibaum.admin_server

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.client.discovery.EnableDiscoveryClient


@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
class AdminServerApplication

fun main(args: Array<String>) {
	SpringApplicationBuilder(AdminServerApplication::class.java)
			.web(WebApplicationType.REACTIVE)
			.run(*args)
}
