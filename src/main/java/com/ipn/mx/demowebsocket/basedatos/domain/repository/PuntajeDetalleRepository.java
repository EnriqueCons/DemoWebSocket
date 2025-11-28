package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PuntajeDetalleRepository extends JpaRepository<PuntajeDetalle, Long> {
    Long countByAlumnoIdAlumno(Long alumnoId);

    Optional<PuntajeDetalle> findTopByAlumnoIdAlumnoOrderByIdPuntajeDesc(Long alumnoId);
}