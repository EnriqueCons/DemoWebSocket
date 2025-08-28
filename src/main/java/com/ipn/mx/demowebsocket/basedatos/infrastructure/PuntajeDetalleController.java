package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import com.ipn.mx.demowebsocket.basedatos.service.PuntajeDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiPuntajes")
public class PuntajeDetalleController {
    @Autowired
    private PuntajeDetalleService service;

    @GetMapping("/puntaje")
    @ResponseStatus(HttpStatus.OK)
    public List<PuntajeDetalle> readAll() { return service.readAll(); }

    @GetMapping("/puntaje/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PuntajeDetalle read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/puntaje")
    @ResponseStatus(HttpStatus.CREATED)
    public PuntajeDetalle save(@RequestBody PuntajeDetalle pd) { return service.save(pd); }

    @PutMapping("/puntaje/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public PuntajeDetalle update(@PathVariable Integer id, @RequestBody PuntajeDetalle pd) {
        PuntajeDetalle p = service.read(id);
        p.setCombate(pd.getCombate());
        p.setAlumno(pd.getAlumno());
        p.setValorPuntaje(pd.getValorPuntaje());
        return service.save(p);
    }

    @DeleteMapping("/puntaje/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
