package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Participacion")
public class Participacion implements Serializable {

    @EmbeddedId
    private ParticipacionId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("idCombate")
    @JoinColumn(name = "idCombate",
            foreignKey = @ForeignKey(name = "fk_part_combate"))
    private Combate combate;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("idAlumno")
    @JoinColumn(name = "idAlumno",
            foreignKey = @ForeignKey(name = "fk_part_alumno"))
    private Alumno alumno;
}
