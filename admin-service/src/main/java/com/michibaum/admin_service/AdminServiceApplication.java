package com.michibaum.admin_service;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
@EnableScheduling
public class AdminServiceApplication {

    /**
     * Main starting Spring Boot Application
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }

}
