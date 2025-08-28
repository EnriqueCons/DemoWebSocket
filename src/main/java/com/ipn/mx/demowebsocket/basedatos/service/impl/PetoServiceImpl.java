package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Peto;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.PetoRepository;
import com.ipn.mx.demowebsocket.basedatos.service.PetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetoServiceImpl implements PetoService {
    @Autowired
    private PetoRepository petoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Peto> readAll() {
        return petoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Peto read(Integer id) {
        return petoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Peto save(Peto peto) {
        return petoRepository.save(peto);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        petoRepository.deleteById(id);
    }
}
