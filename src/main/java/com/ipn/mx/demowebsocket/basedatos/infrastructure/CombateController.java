package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.*;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.CombateJuezRepository;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.ParticipacionRepository;
import com.ipn.mx.demowebsocket.basedatos.service.*;
import com.ipn.mx.demowebsocket.servidor.PendingConnectionRegistry;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/apiCombates")
@CrossOrigin(origins = {"*"})
public class CombateController {

    @Autowired private CombateService combateService;
    @Autowired private AlumnoService alumnoService;
    @Autowired private AreaCombateService areaService;
    @Autowired private ParticipacionRepository participacionRepo;
    @Autowired private JuezService juezService;
    @Autowired private CombateJuezRepository combateJuezRepo;
    private final PendingConnectionRegistry pendingConnectionRegistry;
    @Autowired private TorneoService torneoService;

    @Autowired
    public CombateController(
            CombateService combateService,
            AlumnoService alumnoService,
            AreaCombateService areaService,
            ParticipacionRepository participacionRepo,
            JuezService juezService,
            CombateJuezRepository combateJuezRepo,
            PendingConnectionRegistry pendingConnectionRegistry,
            TorneoService torneoService
    ) {
        this.combateService = combateService;
        this.alumnoService = alumnoService;
        this.areaService = areaService;
        this.participacionRepo = participacionRepo;
        this.juezService = juezService;
        this.combateJuezRepo = combateJuezRepo;
        this.pendingConnectionRegistry = pendingConnectionRegistry;
        this.torneoService = torneoService;

    }

    @PostMapping("/combate")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Map<String,Object> save(@RequestBody Map<String,Object> body) {
        // 1) Crear Área
        AreaCombate area = new AreaCombate();
        area.setNombreArea(String.valueOf(body.getOrDefault("area","Área")));

        Integer idTorneo = intOrNull(body.get("idTorneo"));

        Torneo torneo = null;
        if (idTorneo != null) {
            torneo = torneoService.read(idTorneo); // Necesitas inyectar TorneoService
        } else {
            torneo = torneoService.findMostRecent();
        }

        if (torneo != null) {
            area.setTorneo(torneo);
        }

        area = areaService.save(area);

        // 2) Crear Alumnos (ROJO y AZUL) a partir del JSON
        Map<String,Object> rojo = (Map<String,Object>) body.get("competidorRojo");
        Map<String,Object> azul = (Map<String,Object>) body.get("competidorAzul");

        Alumno aRojo = new Alumno();
        aRojo.setNombreAlumno(str(rojo.get("nombres")));
        aRojo.setPaternoAlumno(null);
        aRojo.setMaternoAlumno(null);
        aRojo.setSexo(str(rojo.get("sexo")));
        aRojo.setPeso(toBigDecimal(rojo.get("pesoKg")));
        aRojo.setFechaNacimiento(toLocalDate(rojo.get("fechaNacimiento")));
        aRojo = alumnoService.save(aRojo);

        Alumno aAzul = new Alumno();
        aAzul.setNombreAlumno(str(azul.get("nombres")));
        aAzul.setPaternoAlumno(null);
        aAzul.setMaternoAlumno(null);
        aAzul.setSexo(str(azul.get("sexo")));
        aAzul.setPeso(toBigDecimal(azul.get("pesoKg")));
        aAzul.setFechaNacimiento(toLocalDate(azul.get("fechaNacimiento")));
        aAzul = alumnoService.save(aAzul);

        // 3) Crear Combate
        Combate c = new Combate();
        c.setAreaCombate(area);
        c.setNumeroRound(intOrNull(body.get("numeroRound"))); // o "numeroRounds"
        c.setDuracionRound(toLocalTime(body.get("duracionRound")));       // "HH:mm:ss"
        c.setDuracionDescanso(toLocalTime(body.get("duracionDescanso"))); // "HH:mm:ss"
        c.setHoraCombate(toLocalDateTime(body.get("horaCombate")));       // "YYYY-MM-DDTHH:mm:ss"
        c.setEstado(strOrDefault(body.get("estado"), "EN_CURSO"));
        c = combateService.save(c);

        // 4) Crear Participaciones (ROJO / AZUL) enlazando alumnos con combate
        Participacion pRojo = new Participacion();
        pRojo.setId(new ParticipacionId(
                c.getIdCombate(),
                aRojo.getIdAlumno()
        ));
        pRojo.setCombate(c);
        pRojo.setAlumno(aRojo);
        pRojo.setColor("ROJO");
        participacionRepo.save(pRojo);

        Participacion pAzul = new Participacion();
        pAzul.setId(new ParticipacionId(
                c.getIdCombate(),
                aAzul.getIdAlumno()
        ));
        pAzul.setCombate(c);
        pAzul.setAlumno(aAzul);
        pAzul.setColor("AZUL");
        participacionRepo.save(pAzul);

        // 5) Crear JUECES y asociarlos al combate (CENTRAL, J1, J2, J3)
        Map<String, Object> jueces = (Map<String, Object>) body.get("jueces");
        if (jueces != null) {
            // Central
            crearYAsociarJuez(c, jueces.get("arbitroCentral"), "CENTRAL");
            // J1
            crearYAsociarJuez(c, jueces.get("juez1"), "J1");
            // J2
            crearYAsociarJuez(c, jueces.get("juez2"), "J2");
            // J3
            crearYAsociarJuez(c, jueces.get("juez3"), "J3");
        }
        Map<String,Object> resp = new HashMap<>();
        resp.put("id", c.getIdCombate());
        resp.put("idCombate", c.getIdCombate());
        resp.put("idAlumnoRojo", aRojo.getIdAlumno());
        resp.put("idAlumnoAzul", aAzul.getIdAlumno());
        resp.put("duracionRound", body.get("duracionRound"));
        resp.put("duracionDescanso", body.get("duracionDescanso"));
        resp.put("numeroRounds", body.get("numeroRound"));
        resp.put("area", area.getNombreArea());
        resp.put("estado", c.getEstado());

        if (torneo != null) {
            resp.put("idTorneo", torneo.getIdTorneo());
            resp.put("nombreTorneo", torneo.getNombre());
        }

        return resp;
    }

    @PostMapping("/combate/{id}/prepare")
    public ResponseEntity<String> prepareWebSocketConnection(@PathVariable Integer id) {
        Combate combate = combateService.read(id);
        if (combate != null) {
            Long combateId = Long.valueOf(id);
            pendingConnectionRegistry.prepareForRojo(combateId);
            pendingConnectionRegistry.prepareForAzul(combateId);
            return ResponseEntity.ok("Servidor listo para recibir conexiones para el combate " + id);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Combate con id " + id + " no encontrado.");
        }
    }

    // Agregar estos métodos a tu CombateController

    @GetMapping("/combates")
    public ResponseEntity<List<Map<String, Object>>> getAllCombates() {
        List<Combate> combates = combateService.readAll();
        List<Map<String, Object>> response = combates.stream()
                .map(this::combateToMap)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/combate/{id}")
    public ResponseEntity<Map<String, Object>> getCombateById(@PathVariable Integer id) {
        Combate combate = combateService.read(id);
        if (combate == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(combateToMap(combate));
    }

    @DeleteMapping("/combate/{id}")
    public ResponseEntity<Void> deleteCombate(@PathVariable Integer id) {
        Combate combate = combateService.read(id);
        if (combate == null) {
            return ResponseEntity.notFound().build();
        }
        combateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/combate/{id}")
    @Transactional
    public ResponseEntity<Map<String, Object>> updateCombate(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {

        Combate combate = combateService.read(id);
        if (combate == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizar campos permitidos
        if (body.containsKey("numeroRound")) {
            combate.setNumeroRound(intOrNull(body.get("numeroRound")));
        }
        if (body.containsKey("duracionRound")) {
            combate.setDuracionRound(toLocalTime(body.get("duracionRound")));
        }
        if (body.containsKey("duracionDescanso")) {
            combate.setDuracionDescanso(toLocalTime(body.get("duracionDescanso")));
        }
        if (body.containsKey("estado")) {
            combate.setEstado(strOrDefault(body.get("estado"), combate.getEstado()));
        }
        if (body.containsKey("area") && combate.getAreaCombate() != null) {
            combate.getAreaCombate().setNombreArea(String.valueOf(body.get("area")));
        }

        combate = combateService.save(combate);
        return ResponseEntity.ok(combateToMap(combate));
    }


    @GetMapping("/combates/area/{nombreArea}")
    public ResponseEntity<List<Map<String, Object>>> getCombatesByArea(@PathVariable String nombreArea) {
        List<Combate> combates = combateService.findByAreaNombre(nombreArea);
        List<Map<String, Object>> response = combates.stream()
                .map(this::combateToMap)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/combates/estado/{estado}")
    public ResponseEntity<List<Map<String, Object>>> getCombatesByEstado(@PathVariable String estado) {
        List<Combate> combates = combateService.findByEstado(estado);
        List<Map<String, Object>> response = combates.stream()
                .map(this::combateToMap)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/combates/torneo/{idTorneo}")
    public ResponseEntity<List<Map<String, Object>>> getCombatesByTorneo(@PathVariable Integer idTorneo) {
        List<Combate> combates = combateService.findByTorneoId(idTorneo);
        List<Map<String, Object>> response = combates.stream()
                .map(this::combateToMap)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Método helper para convertir Combate a Map
    private Map<String, Object> combateToMap(Combate c) {
        Map<String, Object> map = new HashMap<>();
        map.put("idCombate", c.getIdCombate());
        map.put("numeroRound", c.getNumeroRound());
        map.put("duracionRound", c.getDuracionRound());
        map.put("duracionDescanso", c.getDuracionDescanso());
        map.put("horaCombate", c.getHoraCombate());
        map.put("estado", c.getEstado());

        // Área - CORREGIDO: usar idAreaCombate
        if (c.getAreaCombate() != null) {
            map.put("area", c.getAreaCombate().getNombreArea());
            map.put("idArea", c.getAreaCombate().getIdAreaCombate());  // CAMBIO AQUÍ
        }

        if (c.getAreaCombate().getTorneo() != null) {
            map.put("idTorneo", c.getAreaCombate().getTorneo().getIdTorneo());
        }

        // Participaciones (competidores)
        List<Participacion> participaciones = participacionRepo
                .findByCombate_IdCombate(c.getIdCombate());

        for (Participacion p : participaciones) {
            String color = p.getColor();
            Alumno alumno = p.getAlumno();

            Map<String, Object> competidor = new HashMap<>();
            competidor.put("id", alumno.getIdAlumno());
            competidor.put("nombres", alumno.getNombreAlumno());
            competidor.put("sexo", alumno.getSexo());
            competidor.put("pesoKg", alumno.getPeso());
            competidor.put("fechaNacimiento", alumno.getFechaNacimiento());

            if ("ROJO".equals(color)) {
                map.put("competidorRojo", competidor);
            } else if ("AZUL".equals(color)) {
                map.put("competidorAzul", competidor);
            }
        }

        // Jueces
        List<CombateJuez> juecesAsignados = combateJuezRepo
                .findByCombate_IdCombate(c.getIdCombate());

        Map<String, Object> jueces = new HashMap<>();
        for (CombateJuez cj : juecesAsignados) {
            Juez juez = cj.getJuez();
            Map<String, Object> juezData = new HashMap<>();
            juezData.put("id", juez.getIdJuez());
            juezData.put("nombres", juez.getNombre());
            juezData.put("apellidos", juez.getApellidoPaterno());

            String rol = cj.getRol();
            switch (rol) {
                case "CENTRAL" -> jueces.put("arbitroCentral", juezData);
                case "J1" -> jueces.put("juez1", juezData);
                case "J2" -> jueces.put("juez2", juezData);
                case "J3" -> jueces.put("juez3", juezData);
            }
        }
        map.put("jueces", jueces);

        return map;
    }

    // ------- helpers simples (sin DTOs) -------
    private String str(Object o){ return o==null?null:o.toString().trim(); }
    private String strOrDefault(Object o, String d){ String s=str(o); return (s==null||s.isEmpty())?d:s; }

    private java.time.LocalDate toLocalDate(Object o){
        if (o==null) return null;
        String s=o.toString().trim();
        try { return java.time.LocalDate.parse(s); } catch(Exception ignore){}
        if (s.matches("\\d{2}/\\d{2}/\\d{4}")){
            String[] p=s.split("/");
            return java.time.LocalDate.of(Integer.parseInt(p[2]), Integer.parseInt(p[1]), Integer.parseInt(p[0]));
        }
        return null;
    }
    private java.time.LocalTime toLocalTime(Object o){
        if (o==null) return null;
        String s=o.toString().trim();
        // acepta "HH:mm:ss" o "mm:ss"
        try { return java.time.LocalTime.parse(s); } catch(Exception ignore){}
        if (s.matches("\\d{2}:\\d{2}")){
            return java.time.LocalTime.parse("00:"+s);
        }
        return null;
    }
    private java.time.LocalDateTime toLocalDateTime(Object o){
        if (o==null) return null;
        String s=o.toString().trim();
        // "YYYY-MM-DDTHH:mm:ss"
        try { return java.time.LocalDateTime.parse(s); } catch(Exception ignore){}
        return null;
    }
    private java.math.BigDecimal toBigDecimal(Object o){
        if (o==null) return null;
        return new java.math.BigDecimal(o.toString());
    }
    private Integer intOrNull(Object o){
        if (o==null) return null;
        return Integer.valueOf(o.toString());
    }

    private void crearYAsociarJuez(Combate combate, Object nodo, String rol) {
        if (nodo == null) return;
        Map<String, Object> jmap = (Map<String, Object>) nodo;

        // 1) Crear/guardar Juez
        Juez j = new Juez();
        j.setNombre(str(jmap.get("nombres")));
        // Si en UI viene "apellidos" juntos, guárdalos en paterno
        String apellidos = str(jmap.get("apellidos"));
        j.setApellidoPaterno(apellidos);
        j.setApellidoMaterno(null);
        j = juezService.save(j);

        // 2) Asociar (id compuesto)
        CombateJuez link = new CombateJuez();
        link.setId(new CombateJuezId(combate.getIdCombate(), j.getIdJuez()));
        link.setCombate(combate);
        link.setJuez(j);
        link.setRol(rol);

        // (Opcional) validar que no exista duplicado de rol
        if (combateJuezRepo.existsByCombate_IdCombateAndRol(combate.getIdCombate(), rol)) {
            throw new IllegalStateException("El combate " + combate.getIdCombate() + " ya tiene asignado el rol " + rol);
        }
        combateJuezRepo.save(link);
    }

}
