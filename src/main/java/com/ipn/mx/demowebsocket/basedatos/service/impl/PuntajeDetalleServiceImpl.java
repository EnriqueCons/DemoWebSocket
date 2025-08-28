package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.PuntajeDetalleRepository;
import com.ipn.mx.demowebsocket.basedatos.service.PuntajeDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PuntajeDetalleServiceImpl implements PuntajeDetalleService {
    @Autowired
    private PuntajeDetalleRepository puntajeDetalleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PuntajeDetalle> readAll() {
        return puntajeDetalleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PuntajeDetalle read(Integer id) {
        return puntajeDetalleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public PuntajeDetalle save(PuntajeDetalle puntajeDetalle) {
        return puntajeDetalleRepository.save(puntajeDetalle);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        puntajeDetalleRepository.deleteById(id);
    }
}
