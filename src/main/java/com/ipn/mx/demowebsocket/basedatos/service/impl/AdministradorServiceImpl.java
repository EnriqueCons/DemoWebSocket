package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Administrador;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.AdministradorRepository;
import com.ipn.mx.demowebsocket.basedatos.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdministradorServiceImpl implements AdministradorService {
    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Administrador> readAll() {
        return administradorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Administrador read(Integer id) {
        return administradorRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Administrador save(Administrador administrador) {
        return administradorRepository.save(administrador);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        administradorRepository.deleteById(id);
    }
}
