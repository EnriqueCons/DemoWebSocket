package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.GamJeom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GamJeomRepository extends JpaRepository<GamJeom, Long> {

    Long countByAlumnoIdAlumno(Long alumnoId);

    Long countByAlumnoIdAlumnoAndCombateIdCombate(Long alumnoId, Long combateId);

    List<GamJeom> findByAlumnoIdAlumnoAndCombateIdCombate(Long alumnoId, Long combateId);

    Optional<GamJeom> findTopByAlumnoIdAlumnoOrderByIdGamJeomDesc(Long alumnoId);

    Optional<GamJeom> findTopByAlumnoIdAlumnoAndCombateIdCombateOrderByIdGamJeomDesc(Long alumnoId, Long combateId);
}