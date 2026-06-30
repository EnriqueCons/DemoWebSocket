package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.TipoPuntaje;
import com.ipn.mx.demowebsocket.basedatos.service.TipoPuntajeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiTiposPuntaje")
public class TipoPuntajeController {

    @Autowired
    private TipoPuntajeService service;

    @GetMapping("/tipoPuntaje")
    @ResponseStatus(HttpStatus.OK)
    public List<TipoPuntaje> readAll() { return service.readAll(); }

    @GetMapping("/tipoPuntaje/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TipoPuntaje read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/tipoPuntaje")
    @ResponseStatus(HttpStatus.CREATED)
    public TipoPuntaje save(@Valid @RequestBody TipoPuntaje tipoPuntaje) {
        return service.save(tipoPuntaje);
    }

    @PutMapping("/tipoPuntaje/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TipoPuntaje update(@PathVariable Integer id, @Valid @RequestBody TipoPuntaje tipoPuntaje) {
        TipoPuntaje x = service.read(id);
        if (x == null) throw new IllegalArgumentException("TipoPuntaje no encontrado");

        x.setNombre(tipoPuntaje.getNombre());
        x.setValor(tipoPuntaje.getValor());
        x.setModoRapido(tipoPuntaje.getModoRapido());
        x.setActivo(tipoPuntaje.getActivo());

        return service.save(x);
    }

    @DeleteMapping("/tipoPuntaje/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
