package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Administrador;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.AdministradorRepository;
import com.ipn.mx.demowebsocket.basedatos.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<Administrador> readAll() {
        return administradorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Administrador read(Integer id) {
        return administradorRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Administrador save(Administrador administrador) {
        // Validaciones de unicidad (create o update)
        if (administrador.getCorreoAdministrador() != null) {
            boolean existsCorreo = administradorRepository.existsByCorreoAdministrador(administrador.getCorreoAdministrador());
            // Si viene con id, es update: permitir mismo correo del mismo registro
            if (existsCorreo) {
                administradorRepository.findByCorreoAdministrador(administrador.getCorreoAdministrador())
                        .filter(a -> administrador.getIdAdministrador() != null
                                && a.getIdAdministrador().equals(administrador.getIdAdministrador()))
                        .orElseThrow(() -> new IllegalArgumentException("El correo ya está registrado"));
            }
        }

        // Si usas usuarioAdministrador
        if (administrador.getUsuarioAdministrador() != null) {
            boolean existsUser = false;
            try { existsUser = administradorRepository.existsByUsuarioAdministrador(administrador.getUsuarioAdministrador()); }
            catch (Exception ignore) {}
            if (existsUser) {
                // permitir mismo usuario del mismo registro en update
                // (si quieres, busca por id aquí igual que con correo)
                throw new IllegalArgumentException("El usuario ya está registrado");
            }
        }

        // Hash de contraseña si viene "plana" (no hash)
        String raw = administrador.getContraseniaAdministrador();
        if (raw != null && !raw.isBlank()) {
            // Heurística simple: si no parece BCrypt, hasheamos
            if (!raw.startsWith("$2a$") && !raw.startsWith("$2b$") && !raw.startsWith("$2y$")) {
                administrador.setContraseniaAdministrador(passwordEncoder.encode(raw));
            }
        }

        return administradorRepository.save(administrador);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        administradorRepository.deleteById(id);
    }
}
