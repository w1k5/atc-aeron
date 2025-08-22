package com.w1k5.atc.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Admin Gateway Application
 * 
 * Provides WebSocket streaming and REST API endpoints for monitoring
 * and controlling the ATC Aeron cluster system.
 */
@SpringBootApplication
@EnableAsync
public class AdminGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminGatewayApplication.class, args);
        System.out.println("🚀 Admin Gateway started successfully!");
        System.out.println("📡 WebSocket endpoint: ws://localhost:8080/ws/admin");
        System.out.println("🌐 Admin interface: http://localhost:8080/admin");
    }
}
