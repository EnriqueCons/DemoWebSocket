package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.TipoPuntaje;
import java.util.List;

public interface TipoPuntajeService {
    List<TipoPuntaje> readAll();
    TipoPuntaje read(Integer id);
    TipoPuntaje save(TipoPuntaje tipoPuntaje);
    void delete(Integer id);
}
