package com.ipn.mx.demowebsocket.servidor;

import com.ipn.mx.demowebsocket.servidor.PendingConnectionRegistry;
import com.ipn.mx.demowebsocket.basedatos.service.ScoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ScoreService scoreService;
    private final PendingConnectionRegistry registry;

    public WebSocketConfig(ScoreService scoreService, PendingConnectionRegistry registry) {
        this.scoreService = scoreService;
        this.registry = registry;
    }

    @Bean
    public RojoHandler rojoHandler() {
        return new RojoHandler(scoreService, registry);
    }

    @Bean
    public AzulHandler azulHandler() {
        return new AzulHandler(scoreService, registry);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry r) {
        r.addHandler(rojoHandler(), "/ws/peto/rojo").setAllowedOrigins("*");
        r.addHandler(azulHandler(), "/ws/peto/azul").setAllowedOrigins("*");
    }
}