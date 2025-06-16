package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Peto;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import com.ipn.mx.demowebsocket.basedatos.service.PetoService;
import com.ipn.mx.demowebsocket.basedatos.service.PuntajeDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiPuntajeDetalles")
public class PuntajeDetalleController {
    @Autowired
    private PuntajeDetalleService service;

    @GetMapping("/puntajeDetalle")
    @ResponseStatus(HttpStatus.OK)
    public List<PuntajeDetalle> readAll() {
        return service.readAll();
    }

    @GetMapping("/puntajeDetalle/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PuntajeDetalle read(@PathVariable Integer id) {
        PuntajeDetalle puntaje = service.read(id);
        return puntaje;
    }

    @PostMapping("/puntajeDetalle")
    @ResponseStatus(HttpStatus.CREATED)
    public PuntajeDetalle save(@RequestBody PuntajeDetalle puntaje) {
        return service.save(puntaje);
    }

    @PutMapping("/puntajeDetalle/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public PuntajeDetalle update(@PathVariable Integer id, @RequestBody PuntajeDetalle puntaje) {
        PuntajeDetalle puntaje1 = service.read(id);
        puntaje1.setTipoPuntaje(puntaje.getTipoPuntaje());
        puntaje1.setValorPuntaje(puntaje.getValorPuntaje());
        puntaje1.setCombate(puntaje.getCombate());
        puntaje1.setHoraRegistro(puntaje.getHoraRegistro());
        return service.save(puntaje1);
    }

    @DeleteMapping("/puntajeDetalle/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}