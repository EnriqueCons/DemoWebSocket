package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Torneo;
import java.util.List;

public interface TorneoService {
    List<Torneo> readAll();
    Torneo read(Integer id);
    Torneo save(Torneo torneo);
    void delete(Integer id);

    Torneo findMostRecent();

}
