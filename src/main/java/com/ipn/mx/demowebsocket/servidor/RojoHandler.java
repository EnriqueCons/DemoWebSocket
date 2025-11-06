package com.ipn.mx.demowebsocket.servidor;

import com.ipn.mx.demowebsocket.basedatos.service.ScoreService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RojoHandler extends TextWebSocketHandler {

    private final ScoreService scoreService;
    private final PendingConnectionRegistry registry; // Añade el registro
    private final Map<WebSocketSession, Long> sessions = new ConcurrentHashMap<>();

    public RojoHandler(ScoreService scoreService, PendingConnectionRegistry registry) {
        this.scoreService = scoreService;
        this.registry = registry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long combateId = registry.claimRojoConnection();

        if (combateId != null) {
            sessions.put(session, combateId);
            System.out.println("[ROJO] Conexión establecida y asociada al combate " + combateId);
        } else {
            System.out.println("[ROJO] ADVERTENCIA: Se recibió una conexión sin un combate preparado. Rechazando.");
            session.sendMessage(new TextMessage("ERR:NO_COMBATE_PREPARED"));
            session.close(CloseStatus.POLICY_VIOLATION.withReason("No hay un combate preparado para esta conexión."));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long combateId = sessions.get(session);
        if (combateId == null) {
            session.sendMessage(new TextMessage("ERR:NO_COMBATE_ASSOCIATED"));
            return;
        }

        String payload = message.getPayload().trim();
        System.out.println("[ROJO] Recibio combate " + combateId + ":" + payload);

        try{
            double impactValue = Double.parseDouble(payload);
            scoreService.processImpact(combateId, "ROJO", impactValue);
            session.sendMessage(new TextMessage("ACK" + payload));
        } catch (NumberFormatException e) {
            System.err.println("[ROJO] Error: Mensaje no numérico recibido: " + payload);
            session.sendMessage(new TextMessage("ERR:INVALID_NUMBER"));
        } catch (Exception e) {
            System.err.println("[ROJO] Error al procesar el impacto: " + e.getMessage());
            session.sendMessage(new TextMessage("ERR:PROCESSING_FAILED"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long combateId = sessions.remove(session);
        System.out.println("[ROJO] Conexión cerrada para combate " + combateId + ": " + status);
    }
}