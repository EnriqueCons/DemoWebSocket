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
        return System.currentTimeMillis();
    }

    @Override
    public List<Combate> findByAreaNombre(String nombreArea) {
        return combateRepository.findByAreaCombate_NombreArea(nombreArea);
    }

    @Override
    public List<Combate> findByEstado(String estado) {
        return combateRepository.findByEstado(estado);
    }

    @Override
    public List<Combate> findByTorneoId(Integer idTorneo) {
        return combateRepository.findByAreaCombate_Torneo_IdTorneo(idTorneo);
    }

}
