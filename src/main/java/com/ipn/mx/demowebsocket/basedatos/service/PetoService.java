package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Peto;

import java.util.List;

public interface PetoService {
    public List<Peto> readAll();
    public Peto read(Integer id);
    public Peto save(Peto peto);
    public void delete(Integer id);
}
