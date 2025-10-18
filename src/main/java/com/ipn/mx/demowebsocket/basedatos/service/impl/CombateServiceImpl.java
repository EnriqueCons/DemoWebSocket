package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.CombateRepository;
import com.ipn.mx.demowebsocket.basedatos.service.CombateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CombateServiceImpl implements CombateService {
    @Autowired
    private CombateRepository combateRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Combate> readAll() {
        return combateRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Combate read(Integer id) {
        return combateRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Combate save(Combate combate) {
        return combateRepository.save(combate);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        combateRepository.deleteById(id);
    }

    @Override
    public Long createNewCombatForRed() {
        // Aquí persistes Combate en BD con estado EN_CURSO, lado rojo “pendiente”, etc.
        // return combateRepository.save(...).getId();
        // Ejemplo stub:
        return System.currentTimeMillis(); // <— reemplaza por ID real de BD
    }
}
