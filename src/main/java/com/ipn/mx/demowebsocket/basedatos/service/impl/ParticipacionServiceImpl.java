package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Participacion;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.ParticipacionId;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.ParticipacionRepository;
import com.ipn.mx.demowebsocket.basedatos.service.ParticipacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipacionServiceImpl implements ParticipacionService {
    @Autowired
    private ParticipacionRepository participacionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Participacion> readAll() {
        return participacionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Participacion read(ParticipacionId id) {
        return participacionRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Participacion save(Participacion participacion) {
        return participacionRepository.save(participacion);
    }

    @Override
    @Transactional
    public void delete(ParticipacionId id) {
        participacionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Participacion> findByCombateId(Integer idCombate) {
        return participacionRepository.findByCombateIdCombate(idCombate);
    }
}