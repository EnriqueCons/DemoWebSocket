package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Administrador;
import com.ipn.mx.demowebsocket.basedatos.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiAdministradores")
public class AdministradorController {
    @Autowired
    private AdministradorService service;

    @GetMapping("/administrador")
    @ResponseStatus(HttpStatus.OK)
    public List<Administrador> readAll() { return service.readAll(); }

    @GetMapping("/administrador/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Administrador read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/administrador")
    @ResponseStatus(HttpStatus.CREATED)
    public Administrador save(@RequestBody Administrador a) { return service.save(a); }

    @PutMapping("/administrador/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Administrador update(@PathVariable Integer id, @RequestBody Administrador a) {
        Administrador x = service.read(id);
        x.setNombreAdministrador(a.getNombreAdministrador());
        x.setPaternoAdministrador(a.getPaternoAdministrador());
        x.setMaternoAdministrador(a.getMaternoAdministrador());
        x.setCorreoAdministrador(a.getCorreoAdministrador());
        x.setContraseniaAdministrador(a.getContraseniaAdministrador());
        return service.save(x);
    }

    @DeleteMapping("/administrador/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
