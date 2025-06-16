package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Peto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetoRepository extends JpaRepository<Peto, Integer> {
}
