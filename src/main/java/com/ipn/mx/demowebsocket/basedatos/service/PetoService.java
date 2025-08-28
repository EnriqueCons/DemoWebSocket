package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Peto;
import java.util.List;

public interface PetoService {
    List<Peto> readAll();
    Peto read(Integer id);
    Peto save(Peto peto);
    void delete(Integer id);
}
