package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Juez;
import java.util.List;

public interface JuezService {
    List<Juez> readAll();
    Juez read(Integer id);
    Juez save(Juez juez);
    void delete(Integer id);
}
