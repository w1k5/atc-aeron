package com.w1k5.atc.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for high-performance message streaming
 * 
 * Optimized for sending many messages quickly over WebSocket streams
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for pub/sub messaging
        config.enableSimpleBroker("/topic", "/queue");
        
        // Set message size limits for high-throughput
        config.setPreservePublishOrder(true);
        
        // Configure application destination prefix
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoints for WebSocket connections
        registry.addEndpoint("/ws/admin")
                .setAllowedOriginPatterns("*") // Allow all origins for development
                .withSockJS(); // Enable SockJS fallback
        
        // Direct WebSocket endpoint for maximum performance
        registry.addEndpoint("/ws/admin/direct")
                .setAllowedOriginPatterns("*");
    }
}
