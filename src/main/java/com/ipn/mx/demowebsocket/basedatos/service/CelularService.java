package com.ipn.mx.demowebsocket.basedatos.service;

public interface CelularService {


    void registrarPunto(Integer combateId, Integer juezId, String color, Integer puntos);


    void registrarIncidencia(Integer combateId, Integer juezId);


    void registrarAdvertencia(Integer combateId);
}
