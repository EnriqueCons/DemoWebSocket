package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Peto;
import com.ipn.mx.demowebsocket.basedatos.service.PetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiPetos")
public class PetoController {
    @Autowired
    private PetoService service;

    @GetMapping("/peto")
    @ResponseStatus(HttpStatus.OK)
    public List<Peto> readAll() { return service.readAll(); }

    @GetMapping("/peto/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Peto read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/peto")
    @ResponseStatus(HttpStatus.CREATED)
    public Peto save(@RequestBody Peto peto) { return service.save(peto); }

    @PutMapping("/peto/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Peto update(@PathVariable Integer id, @RequestBody Peto peto) {
        Peto p = service.read(id);
        p.setNumeroPeto(peto.getNumeroPeto());
        p.setAreaCombate(peto.getAreaCombate());
        return service.save(p);
    }

    @DeleteMapping("/peto/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
