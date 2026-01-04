package com.joyopi.backend.config;

import com.joyopi.backend.handler.ParrotHandler;
import com.joyopi.backend.handler.VoteHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.lang.NonNull;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ParrotHandler parrotHandler;
    private final VoteHandler voteHandler;

    public WebSocketConfig(@NonNull ParrotHandler parrotHandler, @NonNull VoteHandler voteHandler) {
        this.parrotHandler = parrotHandler;
        this.voteHandler = voteHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(parrotHandler, "/parrot-socket")
                .setAllowedOrigins("*");
        registry.addHandler(voteHandler, "/vote-socket")
                .setAllowedOrigins("*");
    }
}
