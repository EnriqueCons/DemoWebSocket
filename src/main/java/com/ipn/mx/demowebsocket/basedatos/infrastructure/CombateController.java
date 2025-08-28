package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.service.CombateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiCombates")
public class CombateController {
    @Autowired
    private CombateService service;

    @GetMapping("/combate")
    @ResponseStatus(HttpStatus.OK)
    public List<Combate> readAll() { return service.readAll(); }

    @GetMapping("/combate/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Combate read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/combate")
    @ResponseStatus(HttpStatus.CREATED)
    public Combate save(@RequestBody Combate combate) { return service.save(combate); }

    @PutMapping("/combate/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Combate update(@PathVariable Integer id, @RequestBody Combate combate) {
        Combate c = service.read(id);
        c.setNumeroRound(combate.getNumeroRound());
        c.setDuracionRound(combate.getDuracionRound());
        c.setDuracionDescanso(combate.getDuracionDescanso());
        c.setHoraCombate(combate.getHoraCombate());
        c.setContraseñaCombate(combate.getContraseñaCombate());
        c.setEstado(combate.getEstado());
        c.setAreaCombate(combate.getAreaCombate());
        return service.save(c);
    }

    @DeleteMapping("/combate/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
