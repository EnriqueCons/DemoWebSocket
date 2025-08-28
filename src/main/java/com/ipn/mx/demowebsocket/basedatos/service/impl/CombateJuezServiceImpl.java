package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.CombateJuez;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.CombateJuezId;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.CombateJuezRepository;
import com.ipn.mx.demowebsocket.basedatos.service.CombateJuezService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CombateJuezServiceImpl implements CombateJuezService {
    @Autowired
    private CombateJuezRepository combateJuezRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CombateJuez> readAll() {
        return combateJuezRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public CombateJuez read(CombateJuezId id) {
        return combateJuezRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public CombateJuez save(CombateJuez combateJuez) {
        return combateJuezRepository.save(combateJuez);
    }

    @Override
    @Transactional
    public void delete(CombateJuezId id) {
        combateJuezRepository.deleteById(id);
    }
}
