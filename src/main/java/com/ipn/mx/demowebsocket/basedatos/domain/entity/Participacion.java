package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Participacion", uniqueConstraints = {
        @UniqueConstraint(name = "uk_participacion_combate_color", columnNames = {"idCombate","color"})
    }
)
public class Participacion implements Serializable {

    @EmbeddedId
    private ParticipacionId id = new ParticipacionId();// (idCombate, idAlumno)

    @MapsId("idCombate") // vincula la parte de la PK al FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCombate", nullable = false)
    private Combate combate;

    @MapsId("idAlumno")  // vincula la parte de la PK al FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAlumno", nullable = false)
    private Alumno alumno;

    @Column(name = "color", length = 10)
    private String color;

}