// com.ipn.mx.demowebsocket.basedatos.domain.repository.CombateJuezRepository
package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombateJuezRepository extends JpaRepository<CombateJuez, CombateJuezId> {
    boolean existsById(CombateJuezId id);
    boolean existsByCombate_IdCombateAndRol(Integer idCombate, String rol);
}
