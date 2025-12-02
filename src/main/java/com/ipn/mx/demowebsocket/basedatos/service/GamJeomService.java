package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.GamJeom;

import java.util.List;
import java.util.Map;

public interface GamJeomService {

    List<GamJeom> readAll();
    GamJeom read(Long id);
    GamJeom save(GamJeom gamJeom);
    void delete(Long id);

    Long countByAlumnoId(Long alumnoId);
    Long countByAlumnoIdAndCombateId(Long alumnoId, Long combateId);
    boolean deleteLastByAlumnoId(Long alumnoId);
    boolean deleteLastByAlumnoIdAndCombateId(Long alumnoId, Long combateId);

    Map<String, Object> addGamJeom(Long combateId, Long alumnoId);
}