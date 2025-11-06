package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Administrador;
import com.ipn.mx.demowebsocket.basedatos.service.AdministradorService;
import jakarta.validation.Valid;
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
    public Administrador save(@Valid @RequestBody Administrador a) {
        return service.save(a); // password se hashea en ServiceImpl
    }

    @PutMapping("/administrador/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Administrador update(@PathVariable Integer id, @Valid @RequestBody Administrador a) {
        Administrador x = service.read(id);
        if (x == null) throw new IllegalArgumentException("Administrador no encontrado");

        x.setNombreAdministrador(a.getNombreAdministrador());
        x.setPaternoAdministrador(a.getPaternoAdministrador());
        x.setMaternoAdministrador(a.getMaternoAdministrador());
        x.setCorreoAdministrador(a.getCorreoAdministrador());

        // si usas usuario
        x.setUsuarioAdministrador(a.getUsuarioAdministrador());

        // si llega nueva contraseña, se re-hashará en service.save
        if (a.getContraseniaAdministrador() != null && !a.getContraseniaAdministrador().isBlank()) {
            x.setContraseniaAdministrador(a.getContraseniaAdministrador());
        }

        return service.save(x);
    }

    @DeleteMapping("/administrador/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
