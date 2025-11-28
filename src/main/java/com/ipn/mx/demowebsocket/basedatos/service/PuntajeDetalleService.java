package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import java.util.List;

public interface PuntajeDetalleService {
    List<PuntajeDetalle> readAll();
    PuntajeDetalle read(Long id);
    PuntajeDetalle save(PuntajeDetalle puntajeDetalle);
    void delete(Long id);

    Long countByAlumnoId(Long alumnoId);

    boolean deleteLastByAlumnoId(Long alumnoId);


}
