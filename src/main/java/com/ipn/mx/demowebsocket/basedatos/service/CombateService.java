package com.ipn.mx.demowebsocket.basedatos.service;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;

import java.util.List;

public interface CombateService {
    public List<Combate> readAll();
    public Combate read(Integer id);
    public Combate save(Combate combate);
    public void delete(Integer id);
}
