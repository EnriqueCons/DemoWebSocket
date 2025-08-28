package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Torneo;
import com.ipn.mx.demowebsocket.basedatos.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiTorneos")
public class TorneoController {
    @Autowired
    private TorneoService service;

    @GetMapping("/torneo")
    @ResponseStatus(HttpStatus.OK)
    public List<Torneo> readAll() { return service.readAll(); }

    @GetMapping("/torneo/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Torneo read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/torneo")
    @ResponseStatus(HttpStatus.CREATED)
    public Torneo save(@RequestBody Torneo t) { return service.save(t); }

    @PutMapping("/torneo/{id}")
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
