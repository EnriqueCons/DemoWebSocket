package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Categoria;
import com.ipn.mx.demowebsocket.basedatos.service.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

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
    public Alumno read(@PathVariable Long id) { return service.read(id); }

    @PostMapping("/alumno")
    @ResponseStatus(HttpStatus.CREATED)
    public Alumno save(@RequestBody Map<String, Object> body) {
        Alumno a = new Alumno();
        a.setNombreAlumno(str(body.get("nombreAlumno")));
        a.setPaternoAlumno(str(body.get("paternoAlumno")));
        a.setMaternoAlumno(str(body.get("maternoAlumno")));
        a.setSexo(str(body.get("sexo")));
        a.setPeso(toBigDecimal(body.get("peso")));
        a.setAltura(toBigDecimal(body.get("alura")));
        a.setNacionalidad(str(body.get("nacionalidad")));

        // fechaNacimiento: espera "YYYY-MM-DD" (te lo mando así desde Kivy)
        a.setFechaNacimiento(toLocalDate(body.get("fechaNacimiento")));

        // categoria: opcional; si te mandan idCategoria lo asignas sin cargar toda la entidad
        Object idCat = body.get("idCategoria");
        if (idCat != null) {
            Categoria c = new Categoria();
            c.setIdCategoria(Long.valueOf(idCat.toString()));
            a.setCategoria(c);
        }

        return service.save(a);
    }

    @PutMapping("/alumno/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Alumno update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Alumno a = service.read(id);
        if (a == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado");

        if (body.containsKey("nombreAlumno"))  a.setNombreAlumno(str(body.get("nombreAlumno")));
        if (body.containsKey("paternoAlumno")) a.setPaternoAlumno(str(body.get("paternoAlumno")));
        if (body.containsKey("maternoAlumno")) a.setMaternoAlumno(str(body.get("maternoAlumno")));
        if (body.containsKey("sexo"))          a.setSexo(str(body.get("sexo")));
        if (body.containsKey("peso"))          a.setPeso(toBigDecimal(body.get("peso")));
        if (body.containsKey("fechaNacimiento")) a.setFechaNacimiento(toLocalDate(body.get("fechaNacimiento")));

        if (body.containsKey("idCategoria")) {
            Object idCat = body.get("idCategoria");
            if (idCat != null) {
                Categoria c = new Categoria();
                c.setIdCategoria(Long.valueOf(idCat.toString()));
                a.setCategoria(c);
            } else {
                a.setCategoria(null);
            }
        }
        return service.save(a);
    }

    @DeleteMapping("/alumno/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }

    // helpers
    private String str(Object o){ return o==null?null:o.toString().trim(); }

    private java.time.LocalDate toLocalDate(Object o){
        if (o==null) return null;
        String s=o.toString().trim();
        try { return java.time.LocalDate.parse(s); } catch(Exception ignore){}
        // permite "dd/MM/yyyy"
        if (s.matches("\\d{2}/\\d{2}/\\d{4}")){
            String[] p=s.split("/");
            return java.time.LocalDate.of(Integer.parseInt(p[2]), Integer.parseInt(p[1]), Integer.parseInt(p[0]));
        }
        return null;
    }
    private java.math.BigDecimal toBigDecimal(Object o){
        if (o==null) return null;
        return new java.math.BigDecimal(o.toString());
    }
}
