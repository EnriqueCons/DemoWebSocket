package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipacionId implements Serializable {
    @Column(name = "idCombate", nullable = false)
    private Integer idCombate;

    @Column(name = "idAlumno", nullable = false)
    private Integer idAlumno;
}
