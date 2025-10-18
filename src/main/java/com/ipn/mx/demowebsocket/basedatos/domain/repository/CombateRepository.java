package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface CombateRepository extends JpaRepository<Combate, Integer> {

    // Devuelve el combate más reciente (por horaCombate) en un área y estado dados
    Optional<Combate> findFirstByAreaCombate_IdAreaCombateAndEstadoOrderByHoraCombateDesc(
            Integer idAreaCombate, String estado);

    // equivalente con "Top"
    Optional<Combate> findTopByAreaCombate_IdAreaCombateAndEstadoOrderByHoraCombateDesc(
            Integer idAreaCombate, String estado);
}
