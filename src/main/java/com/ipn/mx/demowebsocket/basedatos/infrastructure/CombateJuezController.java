package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.CombateJuez;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.CombateJuezId;
import com.ipn.mx.demowebsocket.basedatos.service.CombateJuezService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiCombateJuez")
public class CombateJuezController {
    @Autowired
    private CombateJuezService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CombateJuez> readAll() { return service.readAll(); }

    @GetMapping("/{idCombate}/{idJuez}")
    @ResponseStatus(HttpStatus.OK)
    public CombateJuez read(@PathVariable Integer idCombate, @PathVariable Integer idJuez) {
        return service.read(new CombateJuezId(idCombate, idJuez));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CombateJuez save(@RequestBody CombateJuez cj) { return service.save(cj); }

    @PutMapping("/{idCombate}/{idJuez}")
    @ResponseStatus(HttpStatus.CREATED)
    public CombateJuez update(@PathVariable Integer idCombate, @PathVariable Integer idJuez,
                              @RequestBody CombateJuez cj) {
        CombateJuez actual = service.read(new CombateJuezId(idCombate, idJuez));
        if (actual == null) {
            throw new IllegalArgumentException("No existe CombateJuez con idCombate=" + idCombate + " e idJuez=" + idJuez);
        }

        actual.setRol(cj.getRol());
        return service.save(actual);
    }

    @DeleteMapping("/{idCombate}/{idJuez}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer idCombate, @PathVariable Integer idJuez) {
        service.delete(new CombateJuezId(idCombate, idJuez));
    }
}
