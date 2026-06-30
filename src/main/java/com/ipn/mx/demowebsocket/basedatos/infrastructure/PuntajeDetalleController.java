package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.TipoPuntaje;
import com.ipn.mx.demowebsocket.basedatos.service.CombateService;
import com.ipn.mx.demowebsocket.basedatos.service.PuntajeDetalleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiPuntajes")
public class PuntajeDetalleController {
    @Autowired
    private PuntajeDetalleService service;

    @Autowired
    private CombateService combateService;

    @PersistenceContext
    private EntityManager em;

    @GetMapping("/puntaje")
    @ResponseStatus(HttpStatus.OK)
    public List<PuntajeDetalle> readAll() { return service.readAll(); }

    @GetMapping("/puntaje/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PuntajeDetalle read(@PathVariable Long id) { return service.read(id); }

    @PostMapping("/puntaje")
    @ResponseStatus(HttpStatus.CREATED)
    public PuntajeDetalle save(@RequestBody PuntajeDetalle pd) { return service.save(pd); }

    @PutMapping("/puntaje/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public PuntajeDetalle update(@PathVariable Long id, @RequestBody PuntajeDetalle pd) {
        PuntajeDetalle p = service.read(id);
        p.setCombate(pd.getCombate());
        p.setAlumno(pd.getAlumno());
        p.setValorPuntaje(pd.getValorPuntaje());
        p.setTipoPuntaje(pd.getTipoPuntaje());
        p.setRoundNumero(pd.getRoundNumero());
        p.setFechaRegistro(pd.getFechaRegistro());
        return service.save(p);
    }

    @DeleteMapping("/puntaje/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    @GetMapping("/puntaje/alumno/{alumnoId}/count")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> countByAlumnoId(@PathVariable Long alumnoId) {
        Long count = service.countByAlumnoId(alumnoId);
        return Map.of("alumnoId",alumnoId, "count", count );
    }

    @DeleteMapping("/puntaje/alumno/{alumnoId}/last")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> deleteLastByAlumnoId(@PathVariable Long alumnoId) {
        boolean deleted = service.deleteLastByAlumnoId(alumnoId);
        Long newCount = service.countByAlumnoId(alumnoId);

        return Map.of(
                "deleted", deleted,
                "alumnoId", alumnoId,
                "newCount", newCount
        );
    }

    // CORREGIDO: Agregar puntaje con solo IDs usando getReference
    // [MT] ---------------- saveSimple ahora acepta idTipoPuntaje y roundNumero -------------------
    @PostMapping("/puntaje/simple")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> saveSimple(
            @RequestParam Long combateId,
            @RequestParam Long alumnoId,
            @RequestParam(defaultValue = "1") Integer valorPuntaje,
            @RequestParam(required = false) Integer idTipoPuntaje,
            @RequestParam(defaultValue = "1") Integer roundNumero) {

        PuntajeDetalle pd = new PuntajeDetalle();
        pd.setValorPuntaje(valorPuntaje);
        pd.setRoundNumero(roundNumero);

        Combate combate = em.getReference(Combate.class, combateId.intValue());
        pd.setCombate(combate);

        Alumno alumno = em.getReference(Alumno.class, alumnoId.intValue());
        pd.setAlumno(alumno);

        // [MT] ---------------- asignar tipoPuntaje si viene en el request -------------------
        if (idTipoPuntaje != null) {
            TipoPuntaje tipo = em.getReference(TipoPuntaje.class, idTipoPuntaje);
            pd.setTipoPuntaje(tipo);
        }

        PuntajeDetalle saved = service.save(pd);
        Long newCount = service.countByAlumnoId(alumnoId);

        return Map.of(
                "success", true,
                "idPuntaje", saved.getIdPuntaje(),
                "alumnoId", alumnoId,
                "newCount", newCount
        );
    }

    // [MT] ---------------- GET /apiPuntajes/combate/{id}/score -------------------
    @GetMapping("/combate/{combateId}/score")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getScoreByCombate(@PathVariable Integer combateId) {
        Combate combate = combateService.read(combateId);
        if (combate == null) return Map.of("error", "Combate no encontrado");

        List<PuntajeDetalle> puntos = service.findByCombateId(combateId);
        long[] ids = getIdRojoAzul(combate);

        long scoreRojo = puntos.stream()
                .filter(p -> p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[0])
                .mapToLong(p -> p.getValorPuntaje() != null ? p.getValorPuntaje() : 0).sum();

        long scoreAzul = puntos.stream()
                .filter(p -> p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[1])
                .mapToLong(p -> p.getValorPuntaje() != null ? p.getValorPuntaje() : 0).sum();

        return Map.of("rojo", scoreRojo, "azul", scoreAzul);
    }

    // [MT] ---------------- GET /apiPuntajes/combate/{id}/rounds -------------------
    @GetMapping("/combate/{combateId}/rounds")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getRoundsByCombate(@PathVariable Integer combateId) {
        Combate combate = combateService.read(combateId);
        if (combate == null) return Map.of("error", "Combate no encontrado");

        List<PuntajeDetalle> puntos = service.findByCombateId(combateId);
        long[] ids = getIdRojoAzul(combate);
        int totalRounds = combate.getNumeroRound() != null ? combate.getNumeroRound() : 3;

        Map<Integer, List<PuntajeDetalle>> porRound = puntos.stream()
                .collect(Collectors.groupingBy(p -> p.getRoundNumero() != null ? p.getRoundNumero() : 1));

        Map<String, Object> resultado = new LinkedHashMap<>();
        for (int r = 1; r <= totalRounds; r++) {
            List<PuntajeDetalle> enRound = porRound.getOrDefault(r, Collections.emptyList());
            long rojo = enRound.stream()
                    .filter(p -> p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[0])
                    .mapToLong(p -> p.getValorPuntaje() != null ? p.getValorPuntaje() : 0).sum();
            long azul = enRound.stream()
                    .filter(p -> p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[1])
                    .mapToLong(p -> p.getValorPuntaje() != null ? p.getValorPuntaje() : 0).sum();
            Map<String, Object> roundData = new LinkedHashMap<>();
            roundData.put("rojo", rojo);
            roundData.put("azul", azul);
            resultado.put("round" + r, roundData);
        }
        return resultado;
    }

    // [MT] ---------------- GET /apiPuntajes/combate/{id}/eventos -------------------
    @GetMapping("/combate/{combateId}/eventos")
    @ResponseStatus(HttpStatus.OK)
    public List<Map<String, Object>> getEventosByCombate(@PathVariable Integer combateId) {
        Combate combate = combateService.read(combateId);
        if (combate == null) return List.of(Map.of("error", "Combate no encontrado"));

        List<PuntajeDetalle> puntos = service.findByCombateId(combateId);
        long[] ids = getIdRojoAzul(combate);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");

        return puntos.stream()
                .sorted(Comparator.comparing(p ->
                        p.getFechaRegistro() != null ? p.getFechaRegistro() : java.time.LocalDateTime.MIN))
                .map(p -> {
                    Map<String, Object> ev = new LinkedHashMap<>();
                    ev.put("round", p.getRoundNumero() != null ? p.getRoundNumero() : 1);
                    boolean esRojo = p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[0];
                    ev.put("alumno", esRojo ? "Rojo" : "Azul");
                    ev.put("tipo", p.getTipoPuntaje() != null && p.getTipoPuntaje().getNombre() != null
                            ? p.getTipoPuntaje().getNombre() : "Punto");
                    ev.put("valor", p.getValorPuntaje() != null ? p.getValorPuntaje() : 1);
                    ev.put("hora", p.getFechaRegistro() != null ? p.getFechaRegistro().format(fmt) : null);
                    return ev;
                })
                .collect(Collectors.toList());
    }

    // [MT] ---------------- GET /apiPuntajes/combate/{id}/resumen → todo junto -------------------
    @GetMapping("/combate/{combateId}/resumen")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getResumenByCombate(@PathVariable Integer combateId) {
        Combate combate = combateService.read(combateId);
        if (combate == null) return Map.of("error", "Combate no encontrado");

        List<PuntajeDetalle> puntos = service.findByCombateId(combateId);
        long[] ids = getIdRojoAzul(combate);
        int totalRounds = combate.getNumeroRound() != null ? combate.getNumeroRound() : 3;

        // Score total
        long scoreRojo = puntos.stream()
                .filter(p -> p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[0])
                .mapToLong(p -> p.getValorPuntaje() != null ? p.getValorPuntaje() : 0).sum();
        long scoreAzul = puntos.stream()
                .filter(p -> p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[1])
                .mapToLong(p -> p.getValorPuntaje() != null ? p.getValorPuntaje() : 0).sum();

        // Rounds
        Map<Integer, List<PuntajeDetalle>> porRound = puntos.stream()
                .collect(Collectors.groupingBy(p -> p.getRoundNumero() != null ? p.getRoundNumero() : 1));

        List<Map<String, Object>> rounds = new ArrayList<>();
        for (int r = 1; r <= totalRounds; r++) {
            List<PuntajeDetalle> enRound = porRound.getOrDefault(r, Collections.emptyList());
            long rR = enRound.stream().filter(p -> p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[0])
                    .mapToLong(p -> p.getValorPuntaje() != null ? p.getValorPuntaje() : 0).sum();
            long aR = enRound.stream().filter(p -> p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[1])
                    .mapToLong(p -> p.getValorPuntaje() != null ? p.getValorPuntaje() : 0).sum();
            Map<String, Object> rd = new LinkedHashMap<>();
            rd.put("round", r);
            rd.put("rojo", rR);
            rd.put("azul", aR);
            rounds.add(rd);
        }

        // Eventos
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        List<Map<String, Object>> eventos = puntos.stream()
                .sorted(Comparator.comparing(p ->
                        p.getFechaRegistro() != null ? p.getFechaRegistro() : java.time.LocalDateTime.MIN))
                .map(p -> {
                    Map<String, Object> ev = new LinkedHashMap<>();
                    ev.put("round", p.getRoundNumero() != null ? p.getRoundNumero() : 1);
                    boolean esRojo = p.getAlumno() != null && p.getAlumno().getIdAlumno().longValue() == ids[0];
                    ev.put("alumno", esRojo ? "Rojo" : "Azul");
                    ev.put("tipo", p.getTipoPuntaje() != null && p.getTipoPuntaje().getNombre() != null
                            ? p.getTipoPuntaje().getNombre() : "Punto");
                    ev.put("valor", p.getValorPuntaje() != null ? p.getValorPuntaje() : 1);
                    ev.put("hora", p.getFechaRegistro() != null ? p.getFechaRegistro().format(fmt) : null);
                    return ev;
                })
                .collect(Collectors.toList());

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("scoreActual", Map.of("rojo", scoreRojo, "azul", scoreAzul));
        resumen.put("rounds", rounds);
        resumen.put("eventos", eventos);
        return resumen;
    }

    // [MT] ---------------- helper: leer idAlumno ROJO y AZUL desde participaciones del combate -------------------
    private long[] getIdRojoAzul(Combate combate) {
        long idRojo = -1, idAzul = -1;
        if (combate.getParticipaciones() != null) {
            for (var p : combate.getParticipaciones()) {
                if ("ROJO".equals(p.getColor()) && p.getAlumno() != null)
                    idRojo = p.getAlumno().getIdAlumno().longValue();
                else if ("AZUL".equals(p.getColor()) && p.getAlumno() != null)
                    idAzul = p.getAlumno().getIdAlumno().longValue();
            }
        }
        return new long[]{idRojo, idAzul};
    }

}
