package com.ipn.mx.demowebsocket.basedatos.domain.repository;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
