package com.michibaum.admin

import brave.sampler.Sampler
import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
@RefreshScope
@EnableScheduling
class AdminApplication {

	fun main(args: Array<String>) {
		SpringApplicationBuilder(AdminApplication::class.java)
				.web(WebApplicationType.REACTIVE)
				.run(*args)
	}

	@Bean
	fun defaultSampler(): Sampler = Sampler.ALWAYS_SAMPLE

}
