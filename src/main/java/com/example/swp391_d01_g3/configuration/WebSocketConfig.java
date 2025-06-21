package com.example.swp391_d01_g3.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 🚀 WebSocket Configuration cho Chat System
 * 
 * Cấu hình này cho phép:
 * 1. WebSocket connection qua STOMP protocol
 * 2. Message broker cho real-time messaging
 * 3. SockJS fallback cho browser không hỗ trợ WebSocket
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Cấu hình Message Broker
     * - /topic: Broadcast messages (public channels)
     * - /queue: Point-to-point messages (private chat)
     * - /app: Application destination prefix (client gửi messages)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker cho /topic và /queue destinations
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefix cho application destinations (client gửi messages)
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefix cho user-specific destinations
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Đăng ký STOMP endpoints
     * - /ws: Main WebSocket endpoint
     * - withSockJS(): Fallback option cho browsers không hỗ trợ WebSocket
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint "/ws" với SockJS fallback
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Cho phép all origins (development)
                .withSockJS(); // Enable SockJS fallback
        
        // Endpoint khác không có SockJS (optional)
        registry.addEndpoint("/websocket")
                .setAllowedOriginPatterns("*");
    }
} 