package com.joyopi.backend.config;

import com.joyopi.backend.handler.ParrotHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ParrotHandler parrotHandler;

    public WebSocketConfig(ParrotHandler parrotHandler) {
        this.parrotHandler = parrotHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(parrotHandler, "/parrot-socket")
                .setAllowedOrigins("*");
    }
}
