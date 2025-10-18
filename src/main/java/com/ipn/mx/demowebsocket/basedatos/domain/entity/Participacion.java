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
    private ParticipacionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idCombate")
    @JoinColumn(name = "idCombate", foreignKey = @ForeignKey(name = "fk_part_combate"))
    @JsonBackReference("combate-participaciones") // <-- Nómbrala así
    private Combate combate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idAlumno")
    @JoinColumn(name = "idAlumno", foreignKey = @ForeignKey(name = "fk_part_alumno"))
    @JsonBackReference("alumno-participaciones") // <-- Y esta con otro nombre
    private Alumno alumno;

    @Column(name = "color", length = 10, nullable = false)
    private String color;
}