package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Administrador;
import java.util.List;

public interface AdministradorService {
    List<Administrador> readAll();
    Administrador read(Integer id);
    Administrador save(Administrador administrador);
    void delete(Integer id);
}
