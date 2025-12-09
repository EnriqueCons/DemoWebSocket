package com.ipn.mx.demowebsocket.servidor;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
        // El tablero puede recibir mensajes de ping/pong
        String payload = message.getPayload();
        if ("ping".equals(payload)) {
            session.sendMessage(new TextMessage("pong"));
        }
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

        enviarMensajeACombate(combateId, mensaje);
    }

    /**
     * Notifica al tablero el estado de los jueces conectados
     */
    public void notificarEstadoJueces(Integer combateId, List<Integer> juecesConectados) {
        ConcurrentHashMap<String, WebSocketSession> sessions = combateSessions.get(combateId);

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("[TableroWS] No hay tableros conectados al combate " + combateId);
            return;
        }

        // Convertir lista a formato JSON
        String juecesArray = juecesConectados.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        String mensaje = String.format(
                "{\"event\":\"judges_status\",\"combateId\":%d,\"judges\":%s,\"totalJudges\":%d}",
                combateId, juecesArray, juecesConectados.size()
        );

        System.out.println("[TableroWS] 📢 Notificando estado de jueces: " + mensaje);

        enviarMensajeACombate(combateId, mensaje);
    }

    /**
     * Notifica al tablero que 2+ jueces confirmaron una incidencia
     */
    public void notificarIncidenciaConfirmada(Integer combateId) {
        if (combateId == null) return;

        ConcurrentHashMap<String, WebSocketSession> sessions = combateSessions.get(combateId);

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("[TableroWS] ⚠️ No hay tableros conectados para combate " + combateId);
            return;
        }

        String mensaje = String.format(
                "{\"event\":\"incidencia_confirmada\",\"combateId\":%d}",
                combateId
        );

        System.out.println("[TableroWS] 🚨 Notificando incidencia confirmada: " + mensaje);

        // Enviar a todas las sesiones del combate
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(mensaje));
                    System.out.println("[TableroWS] ✓ Incidencia enviada a sesión " + session.getId());
                } catch (IOException e) {
                    System.err.println("[TableroWS] ✗ Error enviando incidencia a sesión " +
                            session.getId() + ": " + e.getMessage());
                }
            }
        });
    }

    /**
     * Envía un mensaje a todos los tableros conectados a un combate
     */
    private void enviarMensajeACombate(Integer combateId, String mensaje) {
        ConcurrentHashMap<String, WebSocketSession> sessions = combateSessions.get(combateId);

        if (sessions == null || sessions.isEmpty()) {
            return;
        }

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
            String[] parts = uri.split("/");
            for (int i = 0; i < parts.length; i++) {
                if ("tablero".equals(parts[i]) && i + 1 < parts.length) {
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