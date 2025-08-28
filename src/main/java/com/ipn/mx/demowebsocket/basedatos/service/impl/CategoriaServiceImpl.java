package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Categoria;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.CategoriaRepository;
import com.ipn.mx.demowebsocket.basedatos.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> readAll() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria read(Integer id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        categoriaRepository.deleteById(id);
    }
}
