package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.CombateJuez;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.CombateJuezId;
import java.util.List;

public interface CombateJuezService {
    List<CombateJuez> readAll();
    CombateJuez read(CombateJuezId id);
    CombateJuez save(CombateJuez combateJuez);
    void delete(CombateJuezId id);
}
