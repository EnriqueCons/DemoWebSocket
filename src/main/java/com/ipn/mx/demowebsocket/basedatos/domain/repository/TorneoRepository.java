package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TorneoRepository extends JpaRepository<Torneo, Integer> {

    // Obtener el torneo con el ID más alto (el más reciente)
    @Query("SELECT t FROM Torneo t ORDER BY t.idTorneo DESC LIMIT 1")
    Optional<Torneo> findMostRecentTorneo();

    Torneo findTopByOrderByIdTorneoDesc();
}