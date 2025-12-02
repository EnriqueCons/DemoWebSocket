package com.ipn.mx.demowebsocket.basedatos.service.impl;


import com.ipn.mx.demowebsocket.basedatos.service.CelularService;
import org.springframework.stereotype.Service;

@Service
public class CelularServiceImpl implements CelularService {

    @Override
    public void guardarPuntaje(Integer juezId, Integer puntos, String color, Integer combateId) {

        if (puntos < 0 || puntos > 5) {
            System.out.println("Puntaje invalido: " + puntos + " (solo 0 a 5)");
            return;
        }

        System.out.println(
                "Guardando puntaje en BD -> " +
                        "Combate: " + combateId +
                        " | Juez: " + juezId +
                        " | Color: " + color +
                        " | Puntos: " + puntos
        );
    }

    @Override
    public void guardarIncidencia(Integer juezId, Integer combateId) {

        System.out.println(
                "Guardando incidencia -> " +
                        "Combate: " + combateId +
                        " | Juez: " + juezId
        );
    }

    @Override
    public void registrarAdvertencia(Integer combateId) {

        System.out.println(
                "Guardando advertencia general -> " +
                        "Combate: " + combateId
        );
    }

    @Override
    public void guardarPromedio(String color, Integer promedioFinal, Integer combateId) {

        System.out.println(
                "Guardando promedio en BD -> " +
                        "Combate: " + combateId +
                        " | Color: " + color +
                        " | Promedio: " + promedioFinal
        );
    }
}