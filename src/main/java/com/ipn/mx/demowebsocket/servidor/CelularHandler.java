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

        if (msg.equals("INCIDENCIA")) {

            synchronized (juecesQueMarcaronIncidencia) {
                if (juecesQueMarcaronIncidencia.contains(juezId)) {
                    System.out.println("Juez " + juezId + " ya marcó incidencia en este round");
                    return;
                }

                juecesQueMarcaronIncidencia.add(juezId);
                incidenciasTemp++;

                System.out.println("Incidencia: Juez " + juezId + " (Total: " + incidenciasTemp + "/" + idsActivos.size() + ") - Combate: " + combateId);

                // Cuando 2 o más jueces marcan incidencia
                if (juecesQueMarcaronIncidencia.size() >= 2) {
                    System.out.println(" 2+ jueces marcaron incidencia - Notificando al tablero - Combate: " + combateId);

                    // Notificar al tablero para que muestre popup y pause el tiempo
                    tableroHandler.notificarIncidenciaConfirmada(combateId);

                    // Registrar advertencia
                    celularService.registrarAdvertencia(combateId);

                    // Limpiar para el siguiente round
                    juecesQueMarcaronIncidencia.clear();
                    incidenciasTemp = 0;
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
        Map<Integer, Integer> conteoVotos = new HashMap<>();
        int juecesQueVotaron = 0;
        int juecesQueVotaronNull = 0; //Contador de votos NULL
        int sumaPuntos = 0;
        List<Integer> votosValidos = new ArrayList<>();

        synchronized (puntosTemp) {
            for (Integer juezId : idsActivos) {
                if (puntosTemp.containsKey(juezId) &&
                        colorTemp.containsKey(juezId) &&
                        colorTemp.get(juezId).equalsIgnoreCase(color)) {

                    int voto = puntosTemp.get(juezId);

                    if (voto == -1) {
                        // Contar votos NULL
                        juecesQueVotaronNull++;
                        System.out.println("Juez " + juezId + " marcó NULL para " + color);
                    } else if (voto >= 0 && voto <= 5) {
                        // Contar solo votos válidos (no -1/NULL)
                        votosValidos.add(voto);
                        juecesQueVotaron++;
                        sumaPuntos += voto;
                        conteoVotos.put(voto, conteoVotos.getOrDefault(voto, 0) + 1);
                    }
                }
            }
        }

        System.out.println("Jueces que votaron " + color + ": " + juecesQueVotaron + "/" + idsActivos.size());
        System.out.println("Jueces que votaron NULL: " + juecesQueVotaronNull);
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

        // Lógica especial para NULL
        // Si 2 o más jueces votaron NULL → restar 1 punto
        if (juecesQueVotaronNull >= 2) {
            System.out.println(" 2+ jueces votaron NULL - Restando 1 punto de " + color);
            restarUnPunto(color, combateId);

            // Limpiar y salir
            puntosTemp.clear();
            colorTemp.clear();
            juecesQueMarcaronIncidencia.clear();
            incidenciasTemp = 0;
            broadcast("RESET_COMPLETO");
            return;
        }

        // Si solo 1 juez votó NULL y no hay suficientes votos válidos
        if (juecesQueVotaron < 2) {
            System.out.println("Se necesitan al menos 2 jueces con votos válidos para sumar puntos. Solo " + juecesQueVotaron + " votaron válido.");

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
            puntajeFinal = puntajeConsenso;
            System.out.println("✓ Consenso encontrado: " + maxCoincidencias + " jueces votaron " + puntajeFinal);
        } else {
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

        System.out.println("🎯 Puntaje final: " + puntajeFinal + " - Combate: " + combateId);

        // Guardar el puntaje final (solo si es mayor a 0)
        if (puntajeFinal > 0) {
            guardarPuntajeMultiple(color, puntajeFinal, combateId);
        } else {
            System.out.println(" Puntaje final es 0, no se guarda nada");
        }

        // Limpiar votos temporales
        puntosTemp.clear();
        colorTemp.clear();
        juecesQueMarcaronIncidencia.clear();
        incidenciasTemp = 0;

        broadcast("RESET_COMPLETO");
    }

    /**
     *  Resta 1 punto (elimina el último registro) cuando 2+ jueces votan NULL
     */
    private void restarUnPunto(String color, Integer combateId) {
        try {
            String colorNormalizado = normalizarColor(color);

            // Buscar la participación del color
            Optional<Participacion> participacionOpt = participacionRepository
                    .findByCombateIdAndColor(combateId.longValue(), colorNormalizado);

            if (!participacionOpt.isPresent()) {
                System.out.println("[CelularHandler] ✗ No se encontró participación para color: " + colorNormalizado);
                return;
            }

            Participacion participacion = participacionOpt.get();
            Long alumnoId = participacion.getAlumno().getIdAlumno();

            // Llamar al endpoint para eliminar el último puntaje
            String url = String.format("http://localhost:8080/apiPuntajes/puntaje/alumno/%d/last", alumnoId);

            System.out.println("[CelularHandler] DELETE (NULL) -> " + url);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.DELETE,
                    null,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseData = response.getBody();
                Long nuevoTotal = ((Number) responseData.get("newCount")).longValue();

                System.out.println("[CelularHandler] ✓ Punto eliminado por NULL - Nuevo total: " + nuevoTotal);

                // Notificar al tablero
                tableroHandler.notificarCambioPuntaje(combateId, alumnoId, nuevoTotal);
            }

        } catch (Exception e) {
            System.out.println("[CelularHandler] ✗ Error restando punto por NULL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Guarda múltiples registros de 1 punto usando el endpoint correcto
     */
    private void guardarPuntajeMultiple(String color, int puntajeFinal, Integer combateId) {
        try {
            String colorNormalizado = normalizarColor(color);

            // Buscar la participación del color
            Optional<Participacion> participacionOpt = participacionRepository
                    .findByCombateIdAndColor(combateId.longValue(), colorNormalizado);

            if (!participacionOpt.isPresent()) {
                System.out.println("[CelularHandler] ✗ No se encontró participación para color: " + colorNormalizado);
                return;
            }

            Participacion participacion = participacionOpt.get();
            Long alumnoId = participacion.getAlumno().getIdAlumno();

            System.out.println("[CelularHandler] 💾 Guardando " + puntajeFinal + " puntos para alumno " + alumnoId);

            // Guardar N registros de 1 punto cada uno
            for (int i = 0; i < puntajeFinal; i++) {
                String url = String.format(
                        "http://localhost:8080/apiPuntajes/puntaje/simple?combateId=%d&alumnoId=%d&valorPuntaje=1",
                        combateId, alumnoId);

                ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("[CelularHandler] ✓ Registro " + (i + 1) + "/" + puntajeFinal + " guardado");
                } else {
                    System.out.println("[CelularHandler] ✗ Error guardando registro " + (i + 1));
                }
            }

            // Obtener el nuevo total y notificar al tablero
            String countUrl = String.format("http://localhost:8080/apiPuntajes/puntaje/alumno/%d/count", alumnoId);
            ResponseEntity<Map> countResponse = restTemplate.getForEntity(countUrl, Map.class);

            if (countResponse.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> countData = countResponse.getBody();
                Long nuevoTotal = ((Number) countData.get("count")).longValue();

                System.out.println("[CelularHandler]  Nuevo total para alumno " + alumnoId + ": " + nuevoTotal);

                // Notificar al tablero
                tableroHandler.notificarCambioPuntaje(combateId, alumnoId, nuevoTotal);
            }

        } catch (Exception e) {
            System.out.println("[CelularHandler] ✗ Error guardando puntajes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Integer extraerCombateIdDeUrl(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri != null) {
                String path = uri.getPath();
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

    private void notificarEstadoJuecesAlTablero(Integer combateId) {
        if (combateId == null || tableroHandler == null) return;

        List<Integer> juecesConectados = new ArrayList<>(idsActivos);

        System.out.println("[CelularHandler] Notificando al tablero - Combate: " + combateId +
                ", Jueces conectados: " + juecesConectados);

        tableroHandler.notificarEstadoJueces(combateId, juecesConectados);
    }

    private String normalizarColor(String color) {
        if (color == null) return "";
        String c = color.trim().toUpperCase();
        if (c.startsWith("R") || c.contains("ROJO") || c.contains("RED")) return "ROJO";
        if (c.startsWith("A") || c.contains("AZUL") || c.contains("BLUE")) return "AZUL";
        return c;
    }
}