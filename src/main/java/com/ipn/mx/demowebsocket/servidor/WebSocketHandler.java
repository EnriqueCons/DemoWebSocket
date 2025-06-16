package com.ipn.mx.demowebsocket.servidor;

import com.ipn.mx.demowebsocket.datos.receiveData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
  /*
    Se inyecta la clase(servicio) recibirDatos
    y s eva  allamar recibirDatosService
   */
  @Autowired
  private receiveData receiveDataService;

  int i;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    System.out.println("Conexión establecida");
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    System.out.println("Conexión cerrada");
  }

  /*
    Este Método permite persistir en base de datos
   */

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    System.out.println("Mensaje recibido: " + message.getPayload());
    receiveDataService.processMessage(message.getPayload());
    session.sendMessage(new TextMessage("Hola, cliente!"));
  }
  /*
  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    System.out.println("Mensaje recibido: " + message.toString());
    recibirDatos mensaje = new recibirDatos(message);
    mensajeR = mensaje.getMensaje();
    session.sendMessage(new TextMessage("Hola, cliente!"));
  }
  */
  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    System.out.println("Error de transporte");
  }
}
