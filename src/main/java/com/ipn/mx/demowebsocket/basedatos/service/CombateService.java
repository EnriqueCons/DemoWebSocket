package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import java.util.List;

public interface CombateService {
    List<Combate> readAll();
    Combate read(Integer id);
    Combate save(Combate combate);
    void delete(Integer id);

    Long createNewCombatForRed();

    List<Combate> findByAreaNombre(String nombreArea);
    List<Combate> findByEstado(String estado);

    List<Combate> findByTorneoId(Integer idTorneo);

}
