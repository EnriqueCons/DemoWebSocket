package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Participacion;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.ParticipacionId;
import com.ipn.mx.demowebsocket.basedatos.service.ParticipacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiParticipaciones")
public class ParticipacionController {
    @Autowired
    private ParticipacionService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Participacion> readAll() { return service.readAll(); }

    @GetMapping("/{idCombate}/{idAlumno}")
    @ResponseStatus(HttpStatus.OK)
    public Participacion read(@PathVariable Integer idCombate, @PathVariable Integer idAlumno) {
        return service.read(new ParticipacionId(idCombate, idAlumno));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Participacion save(@RequestBody Participacion p) { return service.save(p); }

    @PutMapping("/{idCombate}/{idAlumno}")
    @ResponseStatus(HttpStatus.CREATED)
    public Participacion update(@PathVariable Integer idCombate, @PathVariable Integer idAlumno,
                                @RequestBody Participacion p) {
        Participacion actual = service.read(new ParticipacionId(idCombate, idAlumno));
        actual.setCombate(p.getCombate());
        actual.setAlumno(p.getAlumno());
        return service.save(actual);
    }

    @DeleteMapping("/{idCombate}/{idAlumno}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer idCombate, @PathVariable Integer idAlumno) {
        service.delete(new ParticipacionId(idCombate, idAlumno));
    }
}
