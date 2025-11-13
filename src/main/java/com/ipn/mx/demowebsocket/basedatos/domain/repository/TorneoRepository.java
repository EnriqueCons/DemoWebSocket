package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TorneoRepository extends JpaRepository<Torneo, Integer> {
    Optional<Torneo> findTopByOrderByIdTorneoDesc();


}
