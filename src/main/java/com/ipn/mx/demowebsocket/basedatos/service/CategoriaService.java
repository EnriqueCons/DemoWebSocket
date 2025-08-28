package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Categoria;
import java.util.List;

public interface CategoriaService {
    List<Categoria> readAll();
    Categoria read(Integer id);
    Categoria save(Categoria categoria);
    void delete(Integer id);
}
