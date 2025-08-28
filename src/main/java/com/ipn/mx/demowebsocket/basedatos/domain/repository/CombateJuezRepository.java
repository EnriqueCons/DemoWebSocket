package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.CombateJuez;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.CombateJuezId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombateJuezRepository extends JpaRepository<CombateJuez, CombateJuezId> {
}
