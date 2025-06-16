package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombateRepository extends JpaRepository<Combate, Integer> {
}
