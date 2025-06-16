package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;

import java.util.List;

public interface PuntajeDetalleService {
    public List<PuntajeDetalle> readAll();
    public PuntajeDetalle read(Integer id);
    public PuntajeDetalle save(PuntajeDetalle puntajeDetalle);
    public void delete(Integer id);

}
