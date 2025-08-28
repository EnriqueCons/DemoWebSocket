package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Participacion;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.ParticipacionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipacionRepository extends JpaRepository<Participacion, ParticipacionId> {
}
