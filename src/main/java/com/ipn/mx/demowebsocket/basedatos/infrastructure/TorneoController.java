package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Torneo;
import com.ipn.mx.demowebsocket.basedatos.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.http.MediaType;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiTorneos")
public class TorneoController {
    @Autowired
    private TorneoService service;

    @GetMapping(value="/torneo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<Torneo> readAll() { return service.readAll(); }

    @GetMapping(value="/torneo/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Torneo read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping(
            value = "/torneo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Torneo save(@RequestBody Torneo t) {
        // Si solo te llega admin.id, JPA lo resolverá cuando persistas.
        // Si no llega 'administrador', puedes setearlo aquí si quieres.
        return service.save(t);
    }

    @PutMapping(
            value = "/torneo/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Torneo update(@PathVariable Integer id, @RequestBody Torneo t) {
        Torneo x = service.read(id);
        x.setFechaHora(t.getFechaHora());
        x.setSede(t.getSede());
        x.setEstado(t.getEstado());
        x.setAdministrador(t.getAdministrador());
        return service.save(x);
    }

    @DeleteMapping("/torneo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }


}
