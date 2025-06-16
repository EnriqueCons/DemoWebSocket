package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;

import java.util.List;

public interface AlumnoService {
    public List<Alumno> readAll();
    public Alumno read(Integer id);
    public Alumno save(Alumno alumno);
    public void delete(Integer id);

}
