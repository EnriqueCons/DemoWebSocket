package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference; // Importante
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PuntajeDetalle")
public class PuntajeDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPuntaje")
    private Integer idPuntaje;

    @Column(name = "valorPuntaje")
    private Integer valorPuntaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCombate")
    @JsonBackReference("combate-puntajes")
    private Combate combate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAlumno")
    @JsonBackReference("alumno-puntajes")
    private Alumno alumno;
}