package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Categoria;
import com.ipn.mx.demowebsocket.basedatos.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiCategorias")
public class CategoriaController {
    @Autowired
    private CategoriaService service;

    @GetMapping("/categoria")
    @ResponseStatus(HttpStatus.OK)
    public List<Categoria> readAll() { return service.readAll(); }

    @GetMapping("/categoria/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Categoria read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/categoria")
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria save(@RequestBody Categoria categoria) { return service.save(categoria); }

    @PutMapping("/categoria/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria update(@PathVariable Integer id, @RequestBody Categoria categoria) {
        Categoria c = service.read(id);
        c.setNombreCategoria(categoria.getNombreCategoria());
        c.setClasificacion(categoria.getClasificacion());
        c.setPesoMinimo(categoria.getPesoMinimo());
        c.setPesoMaximo(categoria.getPesoMaximo());
        return service.save(c);
    }

    @DeleteMapping("/categoria/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
