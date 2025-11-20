package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.*;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.CombateJuezRepository;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.ParticipacionRepository;
import com.ipn.mx.demowebsocket.basedatos.service.AlumnoService;
import com.ipn.mx.demowebsocket.basedatos.service.AreaCombateService;
import com.ipn.mx.demowebsocket.basedatos.service.CombateService;
import com.ipn.mx.demowebsocket.basedatos.service.JuezService;
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

    @Autowired
    public CombateController(
            CombateService combateService,
            AlumnoService alumnoService,
            AreaCombateService areaService,
            ParticipacionRepository participacionRepo,
            JuezService juezService,
            CombateJuezRepository combateJuezRepo,
            PendingConnectionRegistry pendingConnectionRegistry
    ) {
        this.combateService = combateService;
        this.alumnoService = alumnoService;
        this.areaService = areaService;
        this.participacionRepo = participacionRepo;
        this.juezService = juezService;
        this.combateJuezRepo = combateJuezRepo;
        this.pendingConnectionRegistry = pendingConnectionRegistry;
    }

    @PostMapping("/combate")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Map<String,Object> save(@RequestBody Map<String,Object> body) {
        // 1) Crear Área
        AreaCombate area = new AreaCombate();
        area.setNombreArea(String.valueOf(body.getOrDefault("area","Área")));
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
