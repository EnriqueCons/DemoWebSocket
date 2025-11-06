package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    Optional<Administrador> findByCorreoAdministrador(String correo);
    Optional<Administrador> findByUsuarioAdministrador(String email);
    boolean existsByCorreoAdministrador(String correo);
    boolean existsByUsuarioAdministrador(String usuario);
}
