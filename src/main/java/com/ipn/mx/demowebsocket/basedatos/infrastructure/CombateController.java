package com.ipn.mx.demowebsocket.basedatos.infrastructure;


import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.service.AlumnoService;
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
    public List<Combate> readAll() {
        return service.readAll();
    }

    @GetMapping("/combate/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Combate read(@PathVariable Integer id) {
        Combate combate = service.read(id);
        return combate;
    }

    @PostMapping("/combate")
    @ResponseStatus(HttpStatus.CREATED)
    public Combate save(@RequestBody Combate combate) {
        return service.save(combate);
    }

    @PutMapping("/combate/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Combate update(@PathVariable Integer id, @RequestBody Combate combate) {
        Combate combate1 = service.read(id);
        combate1.setContraseniaCombate(combate1.getContraseniaCombate());
        combate1.setHoraCombate(combate1.getHoraCombate());
        combate1.setAlumnos(combate1.getAlumnos());
        combate1.setEstado(combate1.getEstado());
        combate1.setDuracionRound(combate1.getDuracionRound());
        combate1.setDuracionDescanso(combate1.getDuracionDescanso());
        combate1.setNumeroRound(combate1.getNumeroRound());
        combate1.setPuntajeCompetidorDos(combate1.getPuntajeCompetidorDos());
        combate1.setPuntajeCompetidorUno(combate1.getPuntajeCompetidorUno());
        combate1.setPuntajeTotal(combate1.getPuntajeTotal());

        return service.save(combate1);
    }

    @DeleteMapping("/combate/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

