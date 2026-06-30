package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.TipoPuntaje;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.TipoPuntajeRepository;
import com.ipn.mx.demowebsocket.basedatos.service.TipoPuntajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TipoPuntajeServiceImpl implements TipoPuntajeService {

    @Autowired
    private TipoPuntajeRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoPuntaje> readAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoPuntaje read(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public TipoPuntaje save(TipoPuntaje tipoPuntaje) {
        return repository.save(tipoPuntaje);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}