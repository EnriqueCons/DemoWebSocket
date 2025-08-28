package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Torneo;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.TorneoRepository;
import com.ipn.mx.demowebsocket.basedatos.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TorneoServiceImpl implements TorneoService {
    @Autowired
    private TorneoRepository torneoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Torneo> readAll() {
        return torneoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Torneo read(Integer id) {
        return torneoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Torneo save(Torneo torneo) {
        return torneoRepository.save(torneo);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        torneoRepository.deleteById(id);
    }
}
