package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Participacion;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.ParticipacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParticipacionRepository extends JpaRepository<Participacion, ParticipacionId> {

    @Query("""
        select p
        from Participacion p
        join fetch p.alumno a
        where p.id.idCombate = :combateId
          and upper(p.color) = :colorUpper
    """)
    Optional<Participacion> findByCombateIdAndColor(@Param("combateId") Long combateId,
                                                    @Param("colorUpper") String colorUpper);
}
