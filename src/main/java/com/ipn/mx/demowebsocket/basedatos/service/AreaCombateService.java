package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.AreaCombate;
import java.util.List;

public interface AreaCombateService {
    List<AreaCombate> readAll();
    AreaCombate read(Integer id);
    AreaCombate save(AreaCombate areaCombate);
    void delete(Integer id);
}
