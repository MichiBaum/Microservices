package com.michibaum.javadoc_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class JavadocServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavadocServiceApplication.class, args);
	}

}
