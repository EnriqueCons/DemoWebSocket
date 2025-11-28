package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.service.CelularService;
import org.springframework.stereotype.Service;

@Service
public class CelularServiceImpl implements CelularService {

    @Override
    public void registrarPunto(Integer combateId, Integer juezId, String color, Integer puntos) {
        System.out.println("💾 Guardando punto en BD → Combate: " + combateId +
                " | Juez: " + juezId +
                " | " + color +
                " = " + puntos);
    }

    @Override
    public void registrarIncidencia(Integer combateId, Integer juezId) {
        System.out.println("💾 Guardando incidencia → Combate: " + combateId +
                " | Juez: " + juezId);
    }

    @Override
    public void registrarAdvertencia(Integer combateId) {
        System.out.println("💾 Guardando advertencia general → Combate: " + combateId);
    }
}
