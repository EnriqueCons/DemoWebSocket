package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Torneo;
import com.ipn.mx.demowebsocket.basedatos.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiTorneos")
public class TorneoController {

    @Autowired
    private TorneoService service;

    @GetMapping(value="/torneo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<Torneo> readAll() {
        return service.readAll();
    }

    @GetMapping(value="/torneo/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Torneo read(@PathVariable Integer id) {
        Torneo torneo = service.read(id);
        if (torneo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Torneo no encontrado con id: " + id);
        }
        return torneo;
    }

    @PostMapping(
            value = "/torneo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Torneo save(@RequestBody Torneo t) {
        return service.save(t);
    }

    // ⭐ ACTUALIZADO: Ahora incluye setNombre()
    @PutMapping(
            value = "/torneo/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public Torneo update(@PathVariable Integer id, @RequestBody Torneo t) {
        Torneo x = service.read(id);

        if (x == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Torneo no encontrado con id: " + id);
        }

        x.setNombre(t.getNombre());
        x.setFechaHora(t.getFechaHora());
        x.setSede(t.getSede());
        x.setEstado(t.getEstado());
        x.setAdministrador(t.getAdministrador());

        return service.save(x);
    }

    // ⭐ DELETE con cascada automática
    @DeleteMapping("/torneo/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        Torneo torneo = service.read(id);
        if (torneo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Torneo no encontrado con id: " + id);
        }
        service.delete(id);
    }

    @GetMapping("/torneo/ultimo")
    public ResponseEntity<Map<String, Object>> getUltimoTorneo() {
        Torneo ultimo = service.findMostRecent();

        if (ultimo == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("idTorneo", ultimo.getIdTorneo());
        response.put("nombre", ultimo.getNombre());
        response.put("fechaHora", ultimo.getFechaHora());
        response.put("sede", ultimo.getSede());

        return ResponseEntity.ok(response);
    }
}