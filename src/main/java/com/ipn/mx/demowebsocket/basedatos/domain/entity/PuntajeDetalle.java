package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PuntajeDetalle")
public class PuntajeDetalle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPuntaje", nullable = false)
    private Integer idPuntaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCombate",
            foreignKey = @ForeignKey(name = "fk_puntaje_combate"))
    private Combate combate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAlumno",
            foreignKey = @ForeignKey(name = "fk_puntaje_alumno"))
    private Alumno alumno;

    @Column(name = "valorPuntaje")
    private Integer valorPuntaje;
}
