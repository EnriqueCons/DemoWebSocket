package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;
import com.ipn.mx.demowebsocket.basedatos.service.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/apiAlumnos")
public class AlumnoController {
    @Autowired
    private AlumnoService service;

    @GetMapping("/alumno")
    @ResponseStatus(HttpStatus.OK)
    public List<Alumno> readAll() { return service.readAll(); }

    @GetMapping("/alumno/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Alumno read(@PathVariable Integer id) { return service.read(id); }

    @PostMapping("/alumno")
    @ResponseStatus(HttpStatus.CREATED)
    public Alumno save(@RequestBody Alumno alumno) { return service.save(alumno); }

    @PutMapping("/alumno/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Alumno update(@PathVariable Integer id, @RequestBody Alumno alumno) {
        Alumno a = service.read(id);
        a.setNombreAlumno(alumno.getNombreAlumno());
        a.setPaternoAlumno(alumno.getPaternoAlumno());
        a.setMaternoAlumno(alumno.getMaternoAlumno());
        a.setFechaNacimiento(alumno.getFechaNacimiento());
        a.setSexo(alumno.getSexo());
        a.setPeso(alumno.getPeso());
        a.setCategoria(alumno.getCategoria());
        return service.save(a);
    }

    @DeleteMapping("/alumno/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { service.delete(id); }
}
