package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface CombateRepository extends JpaRepository<Combate, Integer> {

    // Devuelve el combate más reciente (por horaCombate) en un área y estado dados
    Optional<Combate> findFirstByAreaCombate_IdAreaCombateAndEstadoOrderByHoraCombateDesc(
            Integer idAreaCombate, String estado);

    // equivalente con "Top"
    Optional<Combate> findTopByAreaCombate_IdAreaCombateAndEstadoOrderByHoraCombateDesc(
            Integer idAreaCombate, String estado);

    List<Combate> findByAreaCombate_NombreArea(String nombreArea);
    List<Combate> findByEstado(String estado);

    List<Combate> findByAreaCombate_Torneo_IdTorneo(Integer idTorneo);

    Optional<Combate> findByContrasenaCombate(String contrasenaCombate);

}
