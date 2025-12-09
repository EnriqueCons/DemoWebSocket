package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Participacion;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.AlumnoRepository;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.CombateRepository;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.ParticipacionRepository;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.PuntajeDetalleRepository;
import com.ipn.mx.demowebsocket.basedatos.service.CelularService;
import com.ipn.mx.demowebsocket.servidor.TableroHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CelularServiceImpl implements CelularService {

    private static final String ROJO = "ROJO";
    private static final String AZUL = "AZUL";

    @Autowired
    private CombateRepository combateRepository;

    @Autowired
    private ParticipacionRepository participacionRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private PuntajeDetalleRepository puntajeDetalleRepository;

    @Autowired
    private TableroHandler tableroHandler;

    @Override
    @Transactional
    public void guardarPuntaje(Integer juezId, Integer puntos, String color, Integer combateId) {

        if (puntos < 0 || puntos > 5) {
            System.out.println("[CelularService] ✗ Puntaje inválido: " + puntos);
            return;
        }

        System.out.println("[CelularService] 📝 Juez " + juezId + " marcó " + puntos + " pts " + color + " (Combate: " + combateId + ")");

        // Normalizar color
        String colorNormalizado = normalizarColor(color);

        // Buscar la participación del color correspondiente
        Optional<Participacion> participacionOpt = participacionRepository
                .findByCombateIdAndColor(combateId.longValue(), colorNormalizado);

        if (!participacionOpt.isPresent()) {
            System.out.println("[CelularService] ✗ No se encontró participación para color: " + colorNormalizado + " en combate: " + combateId);
            return;
        }

        Participacion participacion = participacionOpt.get();
        Alumno alumno = participacion.getAlumno();

        if (alumno == null) {
            System.out.println("[CelularService] ✗ No hay alumno asociado a la participación");
            return;
        }

        Long alumnoId = alumno.getIdAlumno();
        System.out.println("[CelularService] ✓ Alumno encontrado: " + alumnoId + " (" + colorNormalizado + ")");

        // Obtener el combate
        Optional<Combate> combateOpt = combateRepository.findById(combateId);
        if (!combateOpt.isPresent()) {
            System.out.println("[CelularService] ✗ Combate no encontrado: " + combateId);
            return;
        }

        Combate combate = combateOpt.get();

        try {
            // Guardar el puntaje en la base de datos
            PuntajeDetalle puntajeDetalle = new PuntajeDetalle();
            puntajeDetalle.setCombate(combate);
            puntajeDetalle.setAlumno(alumno);
            puntajeDetalle.setValorPuntaje(puntos);

            puntajeDetalleRepository.save(puntajeDetalle);

            System.out.println("[CelularService] ✓ Puntaje guardado en BD: " + puntos + " pts para alumno " + alumnoId);

            // Obtener el nuevo conteo total
            Long nuevoCount = puntajeDetalleRepository.countByAlumnoIdAlumno(alumnoId);
            System.out.println("[CelularService] 📊 Nuevo total para alumno " + alumnoId + ": " + nuevoCount);

            // Notificar al tablero
            tableroHandler.notificarCambioPuntaje(combateId, alumnoId, nuevoCount);

        } catch (Exception e) {
            System.out.println("[CelularService] ✗ Error guardando puntaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void guardarIncidencia(Integer juezId, Integer combateId) {
        System.out.println("[CelularService] Juez " + juezId + " marcó incidencia (Combate: " + combateId + ")");
    }

    @Override
    public void registrarAdvertencia(Integer combateId) {
        System.out.println("[CelularService] Advertencia registrada - 2+ incidencias (Combate: " + combateId + ")");
    }

    @Override
    @Transactional
    public void guardarPromedio(String color, Integer promedioFinal, Integer combateId) {
        System.out.println("[CelularService] Promedio final " + color + ": " + promedioFinal + " pts (Combate: " + combateId + ")");

        // Normalizar color
        String colorNormalizado = normalizarColor(color);

        // Buscar la participación del color correspondiente
        Optional<Participacion> participacionOpt = participacionRepository
                .findByCombateIdAndColor(combateId.longValue(), colorNormalizado);

        if (!participacionOpt.isPresent()) {
            System.out.println("[CelularService] ✗ No se encontró participación para color: " + colorNormalizado);
            return;
        }

        Participacion participacion = participacionOpt.get();
        Alumno alumno = participacion.getAlumno();

        if (alumno == null) {
            System.out.println("[CelularService] ✗ No hay alumno asociado a la participación");
            return;
        }

        Long alumnoId = alumno.getIdAlumno();

        // Obtener el combate
        Optional<Combate> combateOpt = combateRepository.findById(combateId);
        if (!combateOpt.isPresent()) {
            System.out.println("[CelularService] ✗ Combate no encontrado: " + combateId);
            return;
        }

        Combate combate = combateOpt.get();

        try {
            // Guardar múltiples registros (uno por cada punto)
            for (int i = 0; i < promedioFinal; i++) {
                PuntajeDetalle puntajeDetalle = new PuntajeDetalle();
                puntajeDetalle.setCombate(combate);
                puntajeDetalle.setAlumno(alumno);
                puntajeDetalle.setValorPuntaje(1); // ← Siempre guardar 1 punto por registro

                puntajeDetalleRepository.save(puntajeDetalle);
                System.out.println("[CelularService] ✓ Registro " + (i+1) + "/" + promedioFinal + " guardado");
            }

            System.out.println("[CelularService] ✓ " + promedioFinal + " registros guardados en BD para alumno " + alumnoId);

            // Obtener el nuevo conteo total
            Long nuevoCount = puntajeDetalleRepository.countByAlumnoIdAlumno(alumnoId);
            System.out.println("[CelularService] 📊 Nuevo total para alumno " + alumnoId + ": " + nuevoCount);

            // Notificar al tablero
            tableroHandler.notificarCambioPuntaje(combateId, alumnoId, nuevoCount);

        } catch (Exception e) {
            System.out.println("[CelularService] ✗ Error guardando promedio: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public Integer validarPasswordCombate(String password) {
        Optional<Combate> combate = combateRepository.findByContrasenaCombate(password);

        if (combate.isPresent()) {
            System.out.println("[CelularService] ✓ Contraseña válida. Combate ID: " + combate.get().getIdCombate());
            return combate.get().getIdCombate();
        }

        System.out.println("[CelularService] ✗ Contraseña no encontrada en ningún combate");
        return null;
    }

    /**
     * Normaliza el color a ROJO o AZUL
     */
    private String normalizarColor(String color) {
        if (color == null) return "";
        String c = color.trim().toUpperCase();
        if (c.startsWith("R") || c.contains("ROJO") || c.contains("RED")) return ROJO;
        if (c.startsWith("A") || c.contains("AZUL") || c.contains("BLUE")) return AZUL;
        return c;
    }
}