package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import com.ipn.mx.demowebsocket.basedatos.service.PuntajeDetalleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiPuntajes")
public class PuntajeDetalleController {
    @Autowired
    private PuntajeDetalleService service;

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

    // ⭐ CORREGIDO: Agregar puntaje con solo IDs usando getReference
    @PostMapping("/puntaje/simple")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> saveSimple(
            @RequestParam Long combateId,
            @RequestParam Long alumnoId,
            @RequestParam(defaultValue = "1") Integer valorPuntaje) {

        PuntajeDetalle pd = new PuntajeDetalle();
        pd.setValorPuntaje(valorPuntaje);

        // ⭐ USAR getReference para obtener referencia sin cargar entidad
        Combate combate = em.getReference(Combate.class, combateId.intValue());
        pd.setCombate(combate);

        Alumno alumno = em.getReference(Alumno.class, alumnoId.intValue());
        pd.setAlumno(alumno);

        PuntajeDetalle saved = service.save(pd);
        Long newCount = service.countByAlumnoId(alumnoId);

        return Map.of(
                "success", true,
                "idPuntaje", saved.getIdPuntaje(),
                "alumnoId", alumnoId,
                "newCount", newCount
        );
    }
}
