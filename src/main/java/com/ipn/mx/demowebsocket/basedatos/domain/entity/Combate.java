package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Combate")
public class Combate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCombate", nullable = false)
    private Integer idCombate;

    @Column(name = "horaCombate", nullable = false)
    private LocalTime horaCombate;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "duracionRound", nullable = false)
    private int duracionRound;

    @Column(name = "numeroRound", nullable = false)
    private int numeroRound;

    @Column(name = "duracionDescanso", nullable = false)
    private int duracionDescanso;

    @Column(name = "puntajeCompetidorUno", nullable = false)
    private int puntajeCompetidorUno;

    @Column(name = "puntajeCompetidorDos", nullable = false)
    private int puntajeCompetidorDos;

    @Column(name = "puntajeTotal", nullable = false)
    private int puntajeTotal;

    @Column(name = "contraseniaCombate", length = 50, nullable = false)
    private String contraseniaCombate;

    @OneToMany
    @JoinColumn(name = "idAlumno", nullable = false)
    private List<Alumno> alumnos;




}
