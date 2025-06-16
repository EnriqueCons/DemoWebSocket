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
    public List<Alumno> readAll() {
        return service.readAll();
    }

    @GetMapping("/alumno/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Alumno read(@PathVariable Integer id) {
        Alumno alumno = service.read(id);
        return alumno;
    }

    @PostMapping("/alumno")
    @ResponseStatus(HttpStatus.CREATED)
    public Alumno save(@RequestBody Alumno alumno) {

        return service.save(alumno);
    }

    @PutMapping("/alumno/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Alumno update(@PathVariable Integer id, @RequestBody Alumno alumno) {
        Alumno alumno1 = service.read(id);
        alumno1.setNombreAlumno(alumno.getNombreAlumno());
        alumno1.setPaternoAlumno(alumno.getPaternoAlumno());
        alumno1.setMaternoAlumno(alumno.getMaternoAlumno());
        alumno1.setFechaNacimiento(alumno.getFechaNacimiento());
        alumno1.setSexo(alumno.getSexo());
        alumno1.setPeso(alumno.getPeso());
        alumno1.setPeto(alumno.getPeto());

        return service.save(alumno1);
    }

    @DeleteMapping("/alumno/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }



}
