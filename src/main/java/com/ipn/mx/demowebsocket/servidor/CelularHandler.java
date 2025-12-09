package com.ipn.mx.demowebsocket.servidor;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Participacion;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.ParticipacionRepository;
import com.ipn.mx.demowebsocket.basedatos.service.CelularService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CelularHandler extends TextWebSocketHandler {

    private final CelularService celularService;
    private final TableroHandler tableroHandler;
    private final ParticipacionRepository participacionRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public CelularHandler(CelularService celularService,
                          TableroHandler tableroHandler,
                          ParticipacionRepository participacionRepository,
                          RestTemplate restTemplate) {
        this.celularService = celularService;
        this.tableroHandler = tableroHandler;
        this.participacionRepository = participacionRepository;
        this.restTemplate = restTemplate;
    }

    private static final Map<WebSocketSession, Integer> sessionToIdMap = new ConcurrentHashMap<>();
    private static final Map<Integer, WebSocketSession> idToSessionMap = new ConcurrentHashMap<>();
    private static final Set<Integer> idsActivos =
            Collections.synchronizedSet(new HashSet<>());

    private static final int MAX_JUECES = 3;

    private static final Set<Integer> juecesSeleccionados =
            Collections.synchronizedSet(new HashSet<>());

    private static final Map<Integer, Integer> puntosTemp = new ConcurrentHashMap<>();
    private static final Map<Integer, String> colorTemp = new ConcurrentHashMap<>();

    private static int incidenciasTemp = 0;
    private static final Set<Integer> juecesQueMarcaronIncidencia =
            Collections.synchronizedSet(new HashSet<>());

    private static final Map<WebSocketSession, Integer> sessionToCombateIdMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        // Extraer combateId de la URL
        Integer combateId = extraerCombateIdDeUrl(session);
        if (combateId == null) {
            System.out.println("No se pudo extraer combateId de la URL");
            session.close(CloseStatus.BAD_DATA.withReason("combateId requerido"));
            return;
        }

        int juezIdAsignado = -1;

        synchronized (idsActivos) {

            if (idsActivos.size() >= MAX_JUECES) {
                session.close(CloseStatus.SERVICE_OVERLOAD.withReason("Sala llena"));
                return;
            }

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
                sessionToCombateIdMap.put(session, combateId);

                System.out.println("Juez conectado con ID: " + juezIdAsignado + " para combate: " + combateId);

                // NOTIFICAR AL TABLERO QUE UN JUEZ SE CONECTÓ
                notificarEstadoJuecesAlTablero(combateId);

                send(session, "ESTADO_JUECES:" + juecesSeleccionados.toString());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        Integer juezId = sessionToIdMap.get(session);
        Integer combateId = sessionToCombateIdMap.get(session);

        if (juezId != null) {

            idsActivos.remove(juezId);
            sessionToIdMap.remove(session);
            idToSessionMap.remove(juezId);
            sessionToCombateIdMap.remove(session);

            puntosTemp.remove(juezId);
            colorTemp.remove(juezId);
            juecesQueMarcaronIncidencia.remove(juezId);
            juecesSeleccionados.remove(juezId);
            broadcast("ESTADO_JUECES:" + juecesSeleccionados.toString());

            System.out.println("Juez desconectado: " + juezId);

            // NOTIFICAR AL TABLERO QUE UN JUEZ SE DESCONECTÓ
            if (combateId != null) {
                notificarEstadoJuecesAlTablero(combateId);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String msg = message.getPayload().trim();
        Integer juezId = sessionToIdMap.get(session);
        Integer combateId = sessionToCombateIdMap.get(session);

        if (juezId == null || combateId == null) return;

        if (msg.startsWith("SELECCIONAR_JUEZ:")) {

            int juezSeleccionado = Integer.parseInt(
                    msg.replace("SELECCIONAR_JUEZ:", "").trim()
            );

            synchronized (juecesSeleccionados) {

                if (juecesSeleccionados.contains(juezSeleccionado)) {
                    send(session, "JUEZ_OCUPADO");
                    System.out.println("Juez " + juezSeleccionado + " ya ocupado");
                    return;
                }

                juecesSeleccionados.add(juezSeleccionado);
                System.out.println("Juez " + juezSeleccionado + " seleccionado por sesión " + juezId);
            }

            broadcast("ESTADO_JUECES:" + juecesSeleccionados.toString());
            return;
        }

        if (msg.startsWith("PUNTUAR:")) {

            try {
                String[] partes = msg.replace("PUNTUAR:", "").split(",");

                if (partes.length < 2) {
                    send(session, "ERROR:FORMATO_INVALIDO");
                    return;
                }

                String puntosStr = partes[0].trim();
                String color = partes[1].trim();

                int puntos = Integer.parseInt(puntosStr);

                // Manejar el caso -1 (sin puntaje seleccionado)
                if (puntos == -1) {
                    System.out.println("Juez " + juezId + " no seleccionó puntaje para " + color + " (Combate: " + combateId + ")");
                    // Guardar -1 para indicar NULL
                    puntosTemp.put(juezId, -1);
                    colorTemp.put(juezId, color);

                    broadcast("PUNTAJE:" + juezId + ",NULL," + color);

                    // Verificar si ya todos votaron
                    calcularYEnviarPromedio(color, combateId);
                    return;
                }

                if (puntos < 0 || puntos > 5) {
                    send(session, "ERROR:PUNTOS_INVALIDOS");
                    return;
                }

                puntosTemp.put(juezId, puntos);
                colorTemp.put(juezId, color);

                broadcast("PUNTAJE:" + juezId + "," + puntos + "," + color);

                System.out.println("Puntaje: Juez " + juezId + " → " + puntos + " pts " + color + " (Combate: " + combateId + ")");

                // Calcular y enviar promedio cuando todos hayan votado
                calcularYEnviarPromedio(color, combateId);

            } catch (NumberFormatException e) {
                System.out.println("Error parseando puntos del juez " + juezId + ": " + e.getMessage());
                send(session, "ERROR:FORMATO_INVALIDO");
            } catch (Exception e) {
                System.out.println("Error procesando puntaje del juez " + juezId + ": " + e.getMessage());
                send(session, "ERROR:PROCESAMIENTO");
            }

            return;
        }

        if (msg.startsWith("INCIDENCIA:")) {

            String color = msg.replace("INCIDENCIA:", "").trim();

            synchronized (juecesQueMarcaronIncidencia) {
                if (juecesQueMarcaronIncidencia.contains(juezId)) {
                    System.out.println("Juez " + juezId + " ya marcó incidencia en este round");
                    return;
                }

                juecesQueMarcaronIncidencia.add(juezId);
                incidenciasTemp++;

                broadcast("INCIDENCIAS:" + incidenciasTemp + "," + color);
                celularService.guardarIncidencia(juezId, combateId);

                System.out.println("Incidencia: Juez " + juezId + " para " + color + " (Total: " + incidenciasTemp + ") - Combate: " + combateId);

                if (juecesQueMarcaronIncidencia.size() >= 2) {
                    // 2 o más jueces marcaron incidencia
                    registrarGamJeom(color, combateId);

                    broadcast("HABILITAR_PUNTOS");
                    celularService.registrarAdvertencia(combateId);
                    System.out.println("GAM-JEOM agregado a " + color + " (2+ incidencias) - Combate: " + combateId);
                }
            }
            return;
        }

        if (msg.equals("RESET")) {

            puntosTemp.clear();
            colorTemp.clear();
            juecesQueMarcaronIncidencia.clear();
            incidenciasTemp = 0;

            broadcast("RESET_COMPLETO");
            System.out.println("Reset completado - Combate: " + combateId);
        }
    }

    private void broadcast(String msg) {
        for (WebSocketSession sesion : idToSessionMap.values()) {
            try {
                if (sesion.isOpen()) {
                    sesion.sendMessage(new TextMessage(msg));
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void send(WebSocketSession session, String msg) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(msg));
            }
        } catch (Exception ignored) {
        }
    }

    private void calcularYEnviarPromedio(String color, Integer combateId) {
        Map<Integer, Integer> conteoVotos = new HashMap<>(); // valor → cantidad de jueces que lo votaron
        int juecesQueVotaron = 0;
        int sumaPuntos = 0;
        List<Integer> votosValidos = new ArrayList<>();

        synchronized (puntosTemp) {
            for (Integer juezId : idsActivos) {
                if (puntosTemp.containsKey(juezId) &&
                        colorTemp.containsKey(juezId) &&
                        colorTemp.get(juezId).equalsIgnoreCase(color)) {

                    int voto = puntosTemp.get(juezId);

                    // Contar solo votos válidos (no -1/NULL)
                    if (voto >= 0 && voto <= 5) {
                        votosValidos.add(voto);
                        juecesQueVotaron++;
                        sumaPuntos += voto;

                        // Contar cuántos jueces votaron por este valor
                        conteoVotos.put(voto, conteoVotos.getOrDefault(voto, 0) + 1);
                    } else {
                        System.out.println("Juez " + juezId + " marcó NULL para " + color);
                    }
                }
            }
        }

        System.out.println("Jueces que votaron " + color + ": " + juecesQueVotaron + "/" + idsActivos.size());
        System.out.println("Votos válidos: " + votosValidos);
        System.out.println("Conteo de votos: " + conteoVotos);

        // Verificar si todos los jueces activos ya votaron (incluyendo NULL)
        int juecesTotalesQueVotaron = 0;
        for (Integer juezId : idsActivos) {
            if (puntosTemp.containsKey(juezId) &&
                    colorTemp.containsKey(juezId) &&
                    colorTemp.get(juezId).equalsIgnoreCase(color)) {
                juecesTotalesQueVotaron++;
            }
        }

        if (juecesTotalesQueVotaron < idsActivos.size()) {
            System.out.println("⏳ Esperando a que todos los jueces voten. " + juecesTotalesQueVotaron + "/" + idsActivos.size());
            return;
        }

        // Se necesitan mínimo 2 jueces con votos válidos (no NULL)
        if (juecesQueVotaron < 2) {
            System.out.println(" Se necesitan al menos 2 jueces para sumar puntos. Solo " + juecesQueVotaron + " votaron válido.");

            // Limpiar votos temporales sin sumar nada
            puntosTemp.clear();
            colorTemp.clear();
            juecesQueMarcaronIncidencia.clear();
            incidenciasTemp = 0;

            broadcast("RESET_COMPLETO");
            return;
        }

        // Verificar si hay consenso (2 o más jueces con el mismo voto)
        Integer puntajeConsenso = null;
        int maxCoincidencias = 0;

        for (Map.Entry<Integer, Integer> entry : conteoVotos.entrySet()) {
            int valor = entry.getKey();
            int cantidad = entry.getValue();

            if (cantidad >= 2 && cantidad > maxCoincidencias) {
                maxCoincidencias = cantidad;
                puntajeConsenso = valor;
            }
        }

        int puntajeFinal;

        if (puntajeConsenso != null) {
            // Hay consenso: 2 o más jueces coinciden
            puntajeFinal = puntajeConsenso;
            System.out.println("✓ Consenso encontrado: " + maxCoincidencias + " jueces votaron " + puntajeFinal);
        } else {
            // No hay consenso: calcular promedio
            double promedio = (double) sumaPuntos / juecesQueVotaron;

            double parteDecimal = promedio - Math.floor(promedio);

            if (parteDecimal < 0.5) {
                puntajeFinal = (int) Math.floor(promedio);
            } else if (parteDecimal >= 0.6) {
                puntajeFinal = (int) Math.ceil(promedio);
            } else {
                puntajeFinal = (int) Math.round(promedio);
            }

            System.out.println("⚖️ Sin consenso, promedio calculado: " + puntajeFinal + " (de " + promedio + ")");
        }

        System.out.println(" Puntaje final: " + puntajeFinal + " - Combate: " + combateId);

        // Guardar el puntaje final
        celularService.guardarPromedio(color, puntajeFinal, combateId);

        // Limpiar votos temporales
        puntosTemp.clear();
        colorTemp.clear();
        juecesQueMarcaronIncidencia.clear();
        incidenciasTemp = 0;

        broadcast("RESET_COMPLETO");
    }

    private Integer extraerCombateIdDeUrl(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri != null) {
                String path = uri.getPath();
                // Espera formato: /ws/juez/{combateId}
                String[] partes = path.split("/");
                if (partes.length >= 3) {
                    return Integer.parseInt(partes[partes.length - 1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error extrayendo combateId de URL: " + e.getMessage());
        }
        return null;
    }

    /**
     * Notifica al tablero del estado de los jueces conectados
     */
    private void notificarEstadoJuecesAlTablero(Integer combateId) {
        if (combateId == null || tableroHandler == null) return;

        // Obtener lista de jueces activos
        List<Integer> juecesConectados = new ArrayList<>(idsActivos);

        System.out.println("[CelularHandler] Notificando al tablero - Combate: " + combateId +
                ", Jueces conectados: " + juecesConectados);

        tableroHandler.notificarEstadoJueces(combateId, juecesConectados);
    }

    /**
     * Registra una falta GAM-JEOM cuando 2 o más jueces marcan incidencia
     */
    private void registrarGamJeom(String color, Integer combateId) {
        try {
            String colorNormalizado = normalizarColor(color);

            // Buscar la participación del color
            Optional<Participacion> participacionOpt = participacionRepository
                    .findByCombateIdAndColor(combateId.longValue(), colorNormalizado);

            if (!participacionOpt.isPresent()) {
                System.out.println("[CelularHandler] ✗ No se encontró participación para: " + colorNormalizado);
                return;
            }

            Participacion participacion = participacionOpt.get();
            Long alumnoId = participacion.getAlumno().getIdAlumno();

            // Llamar a la API para sumar GAM-JEOM
            String url = String.format("http://localhost:8080/apiGamJeom/falta/simple?combateId=%d&alumnoId=%d",
                    combateId, alumnoId);

            System.out.println("[CelularHandler] POST GAM-JEOM -> " + url);

            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("[CelularHandler] ✓ GAM-JEOM registrado para " + colorNormalizado);
            }

        } catch (Exception e) {
            System.out.println("[CelularHandler] ✗ Error registrando GAM-JEOM: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Normaliza el color a ROJO o AZUL
     */
    private String normalizarColor(String color) {
        if (color == null) return "";
        String c = color.trim().toUpperCase();
        if (c.startsWith("R") || c.contains("ROJO") || c.contains("RED")) return "ROJO";
        if (c.startsWith("A") || c.contains("AZUL") || c.contains("BLUE")) return "AZUL";
        return c;
    }
}