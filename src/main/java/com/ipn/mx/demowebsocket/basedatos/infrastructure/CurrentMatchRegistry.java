package com.ipn.mx.demowebsocket.basedatos.infrastructure;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CurrentMatchRegistry {

    private final Map<Long, PetoAssignment> assignments = new ConcurrentHashMap<>();

    public void setAssignment(Long combateId, Long alumnoRojoId, Long alumnoAzulId) {
        assignments.put(combateId, new PetoAssignment(alumnoRojoId, alumnoAzulId));
    }

    public PetoAssignment getAssignment(Long combateId) {
        return assignments.get(combateId);
    }

    public void clearAssignment(Long combateId) {
        assignments.remove(combateId);
    }

    public static class PetoAssignment {
        private final Long alumnoRojoId;
        private final Long alumnoAzulId;

        public PetoAssignment(Long alumnoRojoId, Long alumnoAzulId) {
            this.alumnoRojoId = alumnoRojoId;
            this.alumnoAzulId = alumnoAzulId;
        }
        public Long getAlumnoRojoId() { return alumnoRojoId; }
        public Long getAlumnoAzulId() { return alumnoAzulId; }
    }
}
