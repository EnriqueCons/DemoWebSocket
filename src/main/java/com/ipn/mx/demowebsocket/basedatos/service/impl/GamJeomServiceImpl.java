package com.ipn.mx.demowebsocket.basedatos.service.impl;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Alumno;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.domain.entity.GamJeom;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.GamJeomRepository;
import com.ipn.mx.demowebsocket.basedatos.service.GamJeomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GamJeomServiceImpl implements GamJeomService {

    @Autowired
    private GamJeomRepository gamJeomRepository;

    private static final int MAX_GAMJEOM = 5;

    @Override
    @Transactional(readOnly = true)
    public List<GamJeom> readAll() {
        return gamJeomRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public GamJeom read(Long id) {
        return gamJeomRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public GamJeom save(GamJeom gamJeom) {
        return gamJeomRepository.save(gamJeom);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        gamJeomRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByAlumnoId(Long alumnoId) {
        Long count = gamJeomRepository.countByAlumnoIdAlumno(alumnoId);
        return count != null ? count : 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByAlumnoIdAndCombateId(Long alumnoId, Long combateId) {
        Long count = gamJeomRepository.countByAlumnoIdAlumnoAndCombateIdCombate(alumnoId, combateId);
        return count != null ? count : 0L;
    }

    @Override
    @Transactional
    public boolean deleteLastByAlumnoId(Long alumnoId) {
        Optional<GamJeom> last = gamJeomRepository.findTopByAlumnoIdAlumnoOrderByIdGamJeomDesc(alumnoId);
        if (last.isPresent()) {
            gamJeomRepository.delete(last.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteLastByAlumnoIdAndCombateId(Long alumnoId, Long combateId) {
        Optional<GamJeom> last = gamJeomRepository.findTopByAlumnoIdAlumnoAndCombateIdCombateOrderByIdGamJeomDesc(alumnoId, combateId);
        if (last.isPresent()) {
            gamJeomRepository.delete(last.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Map<String, Object> addGamJeom(Long combateId, Long alumnoId) {
        Map<String, Object> result = new HashMap<>();

        GamJeom gamJeom = new GamJeom();

        Combate combate = new Combate();
        combate.setIdCombate(combateId.intValue());
        gamJeom.setCombate(combate);

        Alumno alumno = new Alumno();
        alumno.setIdAlumno(alumnoId);
        gamJeom.setAlumno(alumno);

        GamJeom saved = gamJeomRepository.save(gamJeom);

        Long totalFaltas = countByAlumnoIdAndCombateId(alumnoId, combateId);
        boolean descalificado = totalFaltas >= MAX_GAMJEOM;

        result.put("success", true);
        result.put("idGamJeom", saved.getIdGamJeom());
        result.put("alumnoId", alumnoId);
        result.put("combateId", combateId);
        result.put("totalFaltas", totalFaltas);
        result.put("descalificado", descalificado);
        result.put("maxFaltas", MAX_GAMJEOM);

        return result;
    }
}
