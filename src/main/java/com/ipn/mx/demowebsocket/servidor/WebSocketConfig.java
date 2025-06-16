package com.ipn.mx.demowebsocket.servidor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  //Se inyecta el componente webSocketHandler
  @Autowired
  private WebSocketHandler webSocketHandler;

  /*
    Se modifica la linea  registry.addHandler(webSocketHandler(), "/ws").setAllowedOrigins("*");
   */
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(webSocketHandler, "/ws").setAllowedOrigins("*");
  }
  /*

   */
  /*
  @Bean
  public WebSocketHandler webSocketHandler() {
    return new WebSocketHandler();
  }
   */
}
