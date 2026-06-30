package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PuntajeDetalleRepository extends JpaRepository<PuntajeDetalle, Long> {
    Long countByAlumnoIdAlumno(Long alumnoId);

    @Query("SELECT p FROM PuntajeDetalle p WHERE p.alumno.idAlumno = :alumnoId ORDER BY p.idPuntaje DESC")
    List<PuntajeDetalle> findByAlumnoIdOrderByIdDesc(@Param("alumnoId") Long alumnoId);

    @Query("SELECT p FROM PuntajeDetalle p WHERE p.combate.idCombate = :combateId")
    List<PuntajeDetalle> findByCombateId(@Param("combateId") Integer combateId);

    @Query("SELECT p FROM PuntajeDetalle p WHERE p.combate.idCombate = :combateId AND p.alumno.idAlumno = :alumnoId")
    List<PuntajeDetalle> findByCombateIdAndAlumnoId(@Param("combateId") Integer combateId, @Param("alumnoId") Long alumnoId);

}