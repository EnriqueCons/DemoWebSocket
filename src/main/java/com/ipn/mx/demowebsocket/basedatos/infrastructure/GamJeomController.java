package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.GamJeom;
import com.ipn.mx.demowebsocket.basedatos.service.GamJeomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiGamJeom")
public class GamJeomController {

    @Autowired
    private GamJeomService service;

    @GetMapping("/falta")
    @ResponseStatus(HttpStatus.OK)
    public List<GamJeom> readAll() {
        return service.readAll();
    }

    @GetMapping("/falta/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GamJeom read(@PathVariable Long id) {
        return service.read(id);
    }

    @DeleteMapping("/falta/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/falta/simple")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> addGamJeom(
            @RequestParam Long combateId,
            @RequestParam Long alumnoId) {

        return service.addGamJeom(combateId, alumnoId);
    }

    @GetMapping("/falta/alumno/{alumnoId}/count")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> countByAlumnoId(@PathVariable Long alumnoId) {
        Long count = service.countByAlumnoId(alumnoId);
        return Map.of("alumnoId", alumnoId, "count", count);
    }

    @GetMapping("/falta/alumno/{alumnoId}/combate/{combateId}/count")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> countByAlumnoIdAndCombateId(
            @PathVariable Long alumnoId,
            @PathVariable Long combateId) {

        Long count = service.countByAlumnoIdAndCombateId(alumnoId, combateId);
        boolean descalificado = count >= 5;

        return Map.of(
                "alumnoId", alumnoId,
                "combateId", combateId,
                "count", count,
                "descalificado", descalificado
        );
    }

    @DeleteMapping("/falta/alumno/{alumnoId}/last")
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

    @DeleteMapping("/falta/alumno/{alumnoId}/combate/{combateId}/last")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> deleteLastByAlumnoIdAndCombateId(
            @PathVariable Long alumnoId,
            @PathVariable Long combateId) {

        boolean deleted = service.deleteLastByAlumnoIdAndCombateId(alumnoId, combateId);
        Long newCount = service.countByAlumnoIdAndCombateId(alumnoId, combateId);

        return Map.of(
                "deleted", deleted,
                "alumnoId", alumnoId,
                "combateId", combateId,
                "newCount", newCount
        );
    }
}
