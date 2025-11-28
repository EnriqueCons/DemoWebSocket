package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.PuntajeDetalle;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.PuntajeDetalleRepository;
import com.ipn.mx.demowebsocket.basedatos.service.PuntajeDetalleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PuntajeDetalleServiceImpl implements PuntajeDetalleService {
    @Autowired
    private PuntajeDetalleRepository puntajeDetalleRepository;

    @PersistenceContext
    private EntityManager em;


    @Override
    @Transactional(readOnly = true)
    public List<PuntajeDetalle> readAll() {
        return puntajeDetalleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PuntajeDetalle read(Long id) {
        return puntajeDetalleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public PuntajeDetalle save(PuntajeDetalle puntajeDetalle) {
        return puntajeDetalleRepository.save(puntajeDetalle);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        puntajeDetalleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByAlumnoId(Long alumnoId) {
        Long count = em.createQuery(
                "select count(p) from PuntajeDetalle p where p.alumno.id = :alumnoId",
                Long.class
        ).setParameter("alumnoId", alumnoId).getSingleResult();
        return count !=  null ? count : 0L;
    }

    @Override
    @Transactional
    public boolean deleteLastByAlumnoId(Long alumnoId) {
        Optional<PuntajeDetalle> last = puntajeDetalleRepository.findTopByAlumnoIdAlumnoOrderByIdPuntajeDesc(alumnoId);

        if (last.isPresent()) {
            puntajeDetalleRepository.delete(last.get());
            System.out.println(" Eliminado último puntaje del alumno " + alumnoId);
            return true;
        }

        System.out.println(" No hay puntajes para eliminar del alumno " + alumnoId);
        return false;
    }
}
