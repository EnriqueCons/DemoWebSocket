package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Juez;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.JuezRepository;
import com.ipn.mx.demowebsocket.basedatos.service.JuezService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JuezServiceImpl implements JuezService {
    @Autowired
    private JuezRepository juezRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Juez> readAll() {
        return juezRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Juez read(Integer id) {
        return juezRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Juez save(Juez juez) {
        return juezRepository.save(juez);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        juezRepository.deleteById(id);
    }
}
