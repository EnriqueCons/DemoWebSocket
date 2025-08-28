package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import java.util.List;

public interface PuntajeDetalleService {
    List<PuntajeDetalle> readAll();
    PuntajeDetalle read(Integer id);
    PuntajeDetalle save(PuntajeDetalle puntajeDetalle);
    void delete(Integer id);
}
