package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Juez;
import com.ipn.mx.demowebsocket.basedatos.service.JuezService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiJueces")
public class JuezController {
    @Autowired
    private JuezService service;

    @GetMapping("/juez")
    @ResponseStatus(HttpStatus.OK)
    public List<Juez> readAll() { return service.readAll(); }

    @GetMapping("/juez/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Juez read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/juez")
    @ResponseStatus(HttpStatus.CREATED)
    public Juez save(@RequestBody Juez juez) { return service.save(juez); }

    @PutMapping("/juez/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Juez update(@PathVariable Integer id, @RequestBody Juez juez) {
        Juez j = service.read(id);
        j.setNombre(juez.getNombre());
        j.setApellidoPaterno(juez.getApellidoPaterno());
        j.setApellidoMaterno(juez.getApellidoMaterno());
        return service.save(j);
    }

    @DeleteMapping("/juez/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
