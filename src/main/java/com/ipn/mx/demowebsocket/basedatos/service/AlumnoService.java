package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;
import java.util.List;

public interface AlumnoService {
    List<Alumno> readAll();
    Alumno read(Integer id);
    Alumno save(Alumno alumno);
    void delete(Integer id);
}
