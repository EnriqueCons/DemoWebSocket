package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.AreaCombate;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.AreaCombateRepository;
import com.ipn.mx.demowebsocket.basedatos.service.AreaCombateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AreaCombateServiceImpl implements AreaCombateService {
    @Autowired
    private AreaCombateRepository areaCombateRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AreaCombate> readAll() {
        return areaCombateRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public AreaCombate read(Integer id) {
        return areaCombateRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public AreaCombate save(AreaCombate areaCombate) {
        return areaCombateRepository.save(areaCombate);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        areaCombateRepository.deleteById(id);
    }
}
