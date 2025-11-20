package com.ipn.mx.demowebsocket.servidor;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler WebSocket para el tablero central
 * Notifica cuando hay cambios en los puntajes de un combate
 */
@Component
public class TableroHandler extends TextWebSocketHandler {

    // Mapa: combateId -> Map de sesiones conectadas
    private final Map<Integer, ConcurrentHashMap<String, WebSocketSession>> combateSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uri = session.getUri().toString();
        Integer combateId = extractCombateId(uri);

        if (combateId != null) {
            combateSessions.putIfAbsent(combateId, new ConcurrentHashMap<>());
            combateSessions.get(combateId).put(session.getId(), session);

            System.out.println("[TableroWS] ✓ Tablero conectado al combate " + combateId +
                    " (sesión: " + session.getId() + ")");

            // Enviar mensaje de confirmación
            String confirmacion = "{\"status\":\"connected\",\"combateId\":" + combateId + "}";
            session.sendMessage(new TextMessage(confirmacion));
        } else {
            System.out.println("[TableroWS] ✗ URL inválida, cerrando conexión");
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // El tablero no envía mensajes, solo recibe notificaciones
        System.out.println("[TableroWS] Mensaje recibido (ignorado): " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Remover sesión de todos los combates
        for (Map.Entry<Integer, ConcurrentHashMap<String, WebSocketSession>> entry : combateSessions.entrySet()) {
            if (entry.getValue().remove(session.getId()) != null) {
                System.out.println("[TableroWS] ✗ Tablero desconectado del combate " + entry.getKey());
            }
        }
    }

    /**
     * Notifica a todos los tableros conectados a un combate que hubo un cambio de puntaje
     *
     * @param combateId ID del combate
     * @param alumnoId ID del alumno que anotó
     * @param nuevoCount Nuevo puntaje total del alumno
     */
    public void notificarCambioPuntaje(Integer combateId, Long alumnoId, Long nuevoCount) {
        ConcurrentHashMap<String, WebSocketSession> sessions = combateSessions.get(combateId);

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("[TableroWS] No hay tableros conectados al combate " + combateId);
            return;
        }

        String mensaje = String.format(
                "{\"event\":\"score_update\",\"combateId\":%d,\"alumnoId\":%d,\"count\":%d}",
                combateId, alumnoId, nuevoCount
        );

        System.out.println("[TableroWS] 📢 Notificando cambio: " + mensaje);

        // Enviar a todas las sesiones conectadas a este combate
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(mensaje));
                } catch (IOException e) {
                    System.err.println("[TableroWS] Error enviando a sesión " + session.getId() + ": " + e.getMessage());
                }
            }
        });
    }

    /**
     * Extrae el combateId de la URL del WebSocket
     * Espera formato: /ws/tablero/{combateId}
     */
    private Integer extractCombateId(String uri) {
        try {
            // Buscar el patrón /ws/tablero/{numero}
            String[] parts = uri.split("/");
            // La URL es algo como: ws://localhost:8080/ws/tablero/5
            // Después de split: ["ws:", "", "localhost:8080", "ws", "tablero", "5"]
            for (int i = 0; i < parts.length; i++) {
                if ("tablero".equals(parts[i]) && i + 1 < parts.length) {
                    // Limpiar posibles query params (?key=value)
                    String idPart = parts[i + 1].split("\\?")[0];
                    return Integer.parseInt(idPart);
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("[TableroWS] Error extrayendo combateId de URI: " + uri);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtiene el número de tableros conectados a un combate
     */
    public int getConnectedTablerosCount(Integer combateId) {
        ConcurrentHashMap<String, WebSocketSession> sessions = combateSessions.get(combateId);
        return (sessions != null) ? sessions.size() : 0;
    }
}