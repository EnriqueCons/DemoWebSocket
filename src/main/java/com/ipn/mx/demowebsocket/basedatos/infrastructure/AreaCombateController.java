package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.AreaCombate;
import com.ipn.mx.demowebsocket.basedatos.service.AreaCombateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiAreas")
public class AreaCombateController {
    @Autowired
    private AreaCombateService service;

    @GetMapping("/area")
    @ResponseStatus(HttpStatus.OK)
    public List<AreaCombate> readAll() { return service.readAll(); }

    @GetMapping("/area/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AreaCombate read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/area")
    @ResponseStatus(HttpStatus.CREATED)
    public AreaCombate save(@RequestBody AreaCombate area) { return service.save(area); }

    @PutMapping("/area/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public AreaCombate update(@PathVariable Integer id, @RequestBody AreaCombate area) {
        AreaCombate a = service.read(id);
        a.setNombreArea(area.getNombreArea());
        a.setTorneo(area.getTorneo());
        return service.save(a);
    }

    @DeleteMapping("/area/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
