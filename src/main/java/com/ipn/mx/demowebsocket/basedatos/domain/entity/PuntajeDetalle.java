package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PuntajeDetalle")
public class PuntajeDetalle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPuntaje", nullable = false)
    private Integer idPuntaje;

    @Column(name = "valorPuntaje", nullable = false)
    private int valorPuntaje;

    @Column(name = "tipoPuntaje", length = 20, nullable = false)
    private String tipoPuntaje;

    @Column(name = "horaRegistro", nullable = false)
    private LocalTime horaRegistro;

    @ManyToOne
    @JoinColumn(name = "idCombate", nullable = false)
    private Combate combate;
}
