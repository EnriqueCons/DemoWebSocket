package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Participacion;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.ParticipacionId;
import java.util.List;

public interface ParticipacionService {
    List<Participacion> readAll();
    Participacion read(ParticipacionId id);
    Participacion save(Participacion participacion);
    void delete(ParticipacionId id);
}
