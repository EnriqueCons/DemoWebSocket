package com.ipn.mx.demowebsocket.servidor;

import com.ipn.mx.demowebsocket.basedatos.service.CelularService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Basado en el archivo original CelularHandler.java
@Component
public class CelularHandler extends TextWebSocketHandler {

    private final CelularService celularService;

    public CelularHandler(CelularService celularService) {
        this.celularService = celularService;
    }

    // --- GESTIÓN DE SESIONES Y IDs ---
    // Mapea la sesión de WebSocket al ID numérico del juez asignado (1, 2 o 3)
    private static final Map<WebSocketSession, Integer> sessionToIdMap = new ConcurrentHashMap<>();
    // Mapea el ID numérico a la sesión (útil para broadcast si fuera necesario enviar a uno específico)
    private static final Map<Integer, WebSocketSession> idToSessionMap = new ConcurrentHashMap<>();
    // Set sincronizado para llevar control de qué IDs (1, 2, 3) están ocupados actualmente
    private static final Set<Integer> idsActivos = Collections.synchronizedSet(new HashSet<>());
    private static final int MAX_JUECES = 3;

    // --- GESTIÓN DE PUNTOS E INCIDENCIAS DEL COMBATE ACTUAL ---
    // Almacena temporalmente los puntos enviados por cada juezId para el punto actual
    private static final Map<Integer, Integer> puntosTemp = new ConcurrentHashMap<>();
    // Contador de incidencias para el momento actual
    private static int incidenciasTemp = 0;
    // Set para asegurar que un mismo juez no cuente doble en la misma incidencia temporal
    private static final Set<Integer> juecesQueMarcaronIncidencia = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        int juezIdAsignado = -1;

        // Bloque sincronizado para asegurar asignación única de IDs 1, 2, 3 en orden de llegada
        synchronized (idsActivos) {
            if (idsActivos.size() >= MAX_JUECES) {
                System.out.println("⛔ Sala llena. Rechazando conexión.");
                session.close(CloseStatus.SERVICE_OVERLOAD.withReason("Sala llena (máximo 3 jueces)"));
                return;
            }

            // Buscar el primer ID disponible entre 1 y 3
            for (int i = 1; i <= MAX_JUECES; i++) {
                if (!idsActivos.contains(i)) {
                    juezIdAsignado = i;
                    break;
                }
            }

            if (juezIdAsignado != -1) {
                idsActivos.add(juezIdAsignado);
                sessionToIdMap.put(session, juezIdAsignado);
                idToSessionMap.put(juezIdAsignado, session);
                System.out.println("✅ Juez conectado. Se le asignó el ID: " + juezIdAsignado);
                // Opcional: Notificar al juez su ID
                // session.sendMessage(new TextMessage("INFO:TU_ID:" + juezIdAsignado));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer juezId = sessionToIdMap.get(session);
        if (juezId != null) {
            // Liberar el ID y los recursos de forma segura
            synchronized (idsActivos) {
                idsActivos.remove(juezId);
            }
            sessionToIdMap.remove(session);
            idToSessionMap.remove(juezId);

            // Limpiar votos pendientes de este juez si se desconecta a mitad de una acción
            puntosTemp.remove(juezId);
            juecesQueMarcaronIncidencia.remove(juezId);

            System.out.println("❌ Juez " + juezId + " desconectado. ID " + juezId + " liberado.");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload().trim();
        Integer juezId = sessionToIdMap.get(session);

        // Seguridad: verificar que la sesión tiene un ID asignado
        if (juezId == null) {
            System.out.println("⚠️ Mensaje recibido de sesión sin ID asignado. Ignorando.");
            return;
        }

        int combateId = 1; // ⚠️ Valor fijo por ahora, idealmente dinámico

        // --- LÓGICA DE PUNTOS ---
        if (msg.startsWith("PUNTO")) {
            // Formato esperado: PUNTO:COLOR:VALOR (Ej: PUNTO:AZUL:3)
            try {
                String[] partes = msg.split(":");
                String color = partes[1];
                int puntos = Integer.parseInt(partes[2]);

                System.out.println("🔵 Juez " + juezId + " marca " + puntos + " puntos para " + color);
                puntosTemp.put(juezId, puntos);

                // Registrar el voto individual en BD
                celularService.registrarPunto(combateId, juezId, color, puntos);

                // Verificar cuórum: Si 2 o más jueces han votado para esta acción
                if (puntosTemp.size() >= 2) {
                    calcularYEnviarPromedio(color);
                }
            } catch (Exception e) {
                System.err.println("Error procesando mensaje de punto: " + msg + " - " + e.getMessage());
            }
        }

        // --- LÓGICA DE INCIDENCIAS ---
        else if (msg.equalsIgnoreCase("INCIDENCIA")) {
            // Asegurar que este juez no haya marcado ya esta incidencia específica
            if (!juecesQueMarcaronIncidencia.contains(juezId)) {
                incidenciasTemp++;
                juecesQueMarcaronIncidencia.add(juezId);
                System.out.println("⚠️ Juez " + juezId + " marca INCIDENCIA. (Total actual: " + incidenciasTemp + ")");

                // Registrar la incidencia individual en BD
                celularService.registrarIncidencia(combateId, juezId);

                // Verificar cuórum: Si 2 o más jueces marcaron incidencia
                if (incidenciasTemp >= 2) {
                    System.out.println("🚨 Incidencia confirmada por cuórum. Enviando ADVERTENCIA.");
                    celularService.registrarAdvertencia(combateId);
                    broadcast("ADVERTENCIA");

                    // Resetear contadores de incidencia
                    incidenciasTemp = 0;
                    juecesQueMarcaronIncidencia.clear();
                }
            } else {
                System.out.println("ℹ️ Juez " + juezId + " intentó marcar incidencia doble vez. Ignorado.");
            }
        }
    }

    /**
     * Calcula el promedio y aplica la regla de redondeo específica:
     * x.5 baja al entero anterior.
     * x.6 o más sube al entero siguiente.
     */
    private void calcularYEnviarPromedio(String color) throws Exception {
        if (puntosTemp.isEmpty()) return;

        // 1. Calcular promedio aritmético exacto
        double suma = puntosTemp.values().stream().mapToDouble(Integer::doubleValue).sum();
        double promedioExacto = suma / puntosTemp.size();

        // 2. Aplicar regla de redondeo personalizada
        int promedioFinal;
        // Obtener la parte decimal (ej. 3.6 -> 0.6, 3.5 -> 0.5)
        double parteDecimal = promedioExacto - Math.floor(promedioExacto);

        // Regla: si decimal < 0.6, redondea hacia abajo (floor). Si >= 0.6, redondea hacia arriba (ceil).
        // Esto cumple: 3.5 baja a 3, 3.6 sube a 4.
        if (parteDecimal < 0.6 - 0.0001) { // Usamos un pequeño epsilon para evitar errores de precisión flotante
            promedioFinal = (int) Math.floor(promedioExacto);
        } else {
            promedioFinal = (int) Math.ceil(promedioExacto);
        }

        System.out.println("🧮 Cálculo: Votos=" + puntosTemp.size() + ", Suma=" + suma +
                ", Promedio exacto=" + String.format("%.2f", promedioExacto) +
                " -> Redondeado a: " + promedioFinal);

        // 3. Limpiar los puntos temporales para la siguiente acción
        puntosTemp.clear();

        // 4. Enviar el resultado final a todos los jueces
        broadcast("PROMEDIO:" + color + ":" + promedioFinal);
    }

    private void broadcast(String msg) {
        sessionToIdMap.keySet().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(msg));
                } catch (IOException e) {
                    System.err.println("Error enviando broadcast a sesión: " + e.getMessage());
                }
            }
        });
    }
}