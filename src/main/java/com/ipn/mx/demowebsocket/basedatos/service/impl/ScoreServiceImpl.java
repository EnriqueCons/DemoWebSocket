package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Participacion;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.AlumnoRepository;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.CombateRepository;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.ParticipacionRepository;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.PuntajeDetalleRepository;
import com.ipn.mx.demowebsocket.basedatos.service.ScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ScoreServiceImpl implements ScoreService {

    private static final double IMPACT_THRESHOLD = 5.0;
    private static final String ROJO = "ROJO";
    private static final String AZUL = "AZUL";

    private final PuntajeDetalleRepository puntajeRepo;
    private final CombateRepository combateRepo;
    private final AlumnoRepository alumnoRepo;
    private final ParticipacionRepository participacionRepo;

    private final Map<Long, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ScoreServiceImpl(PuntajeDetalleRepository puntajeRepo,
                            CombateRepository combateRepo,
                            AlumnoRepository alumnoRepo,
                            ParticipacionRepository participacionRepo) {
        this.puntajeRepo = puntajeRepo;
        this.combateRepo = combateRepo;
        this.alumnoRepo = alumnoRepo;
        this.participacionRepo = participacionRepo;
    }

    @Transactional
    @Override
    public void processImpact(Long combateId, String petoGolpeado, double impact) {
        if (impact <= IMPACT_THRESHOLD) return;

        String ladoGolpeado = normalizaColor(petoGolpeado);
        if (!ladoGolpeado.equals(ROJO) && !ladoGolpeado.equals(AZUL)) return;

        String ladoQueAnota = ladoGolpeado.equals(ROJO) ? AZUL : ROJO;

        Participacion participacionQueAnota = participacionRepo
                .findByCombateIdAndColor(combateId, ladoQueAnota)
                .orElse(null);
        if (participacionQueAnota == null || participacionQueAnota.getAlumno() == null) return;

        Long alumnoQueRecibePuntoId = participacionQueAnota.getAlumno().getIdAlumno();
        if (alumnoQueRecibePuntoId == null) return;

        // 4) Calcula puntos
        int puntosAGuardar = calcularPuntos(impact);
        if (puntosAGuardar == 0) return;

        // 5) Lock por combate
        ReentrantLock lock = locks.computeIfAbsent(combateId, k -> new ReentrantLock());
        lock.lock();
        try {
            Combate combateActual = combateRepo.findById(Math.toIntExact(combateId))
                    .orElseThrow(() -> new RuntimeException("Error Crítico: Combate no encontrado con ID: " + combateId));

            Alumno alumnoPuntuador = alumnoRepo.findById(Math.toIntExact(alumnoQueRecibePuntoId))
                    .orElseThrow(() -> new RuntimeException("Error Crítico: Alumno no encontrado con ID: " + alumnoQueRecibePuntoId));

            PuntajeDetalle pd = new PuntajeDetalle();
            pd.setCombate(combateActual);
            pd.setAlumno(alumnoPuntuador);
            pd.setValorPuntaje(puntosAGuardar);

            puntajeRepo.save(pd);
            System.out.println("[Score] INSERT hecho para combate " + combateId + " (lado golpeado: " + ladoGolpeado + ", anota: " + ladoQueAnota + ")");
        } catch (Exception e) {
            System.out.println("[Score] EXCEPTION al guardar: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    @Override
    public void processImpact(Integer combateId, String petoGolpeado, double impact) {
        if (combateId == null) return;
        processImpact(combateId.longValue(), petoGolpeado, impact);
    }

    private String normalizaColor(String color) {
        if (color == null) return "";
        String c = color.trim().toUpperCase();
        if (c.startsWith("R") || c.contains("ROJO") || c.contains("RED")) return ROJO;
        if (c.startsWith("A") || c.contains("AZUL") || c.contains("BLUE")) return AZUL;
        return c;
    }

    private int calcularPuntos(double impact) {
        if (impact >= 14.0) return 1;
        return 0;
    }
}
