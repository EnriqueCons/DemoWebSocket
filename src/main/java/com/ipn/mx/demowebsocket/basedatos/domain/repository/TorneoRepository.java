package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TorneoRepository extends JpaRepository<Torneo, Integer> {
}
