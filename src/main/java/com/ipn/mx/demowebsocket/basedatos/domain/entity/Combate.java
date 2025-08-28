package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.*;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Combate")
public class Combate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCombate", nullable = false)
    private Integer idCombate;

    @Column(name = "numeroRound")
    private Integer numeroRound;

    @Column(name = "duracionRound")
    private LocalTime duracionRound;

    @Column(name = "duracionDescanso")
    private LocalTime duracionDescanso;

    @Column(name = "horaCombate")
    private LocalDateTime horaCombate;

    @Column(name = "contraseñaCombate", length = 255)
    private String contraseñaCombate;

    @Column(name = "estado", length = 50)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAreaCombate",
            foreignKey = @ForeignKey(name = "fk_combate_area"))
    private AreaCombate areaCombate;

    @OneToMany(mappedBy = "combate", fetch = FetchType.LAZY)
    private List<PuntajeDetalle> puntajes = new ArrayList<>();

    @OneToMany(mappedBy = "combate", fetch = FetchType.LAZY)
    private List<Participacion> participaciones = new ArrayList<>();

    @OneToMany(mappedBy = "combate", fetch = FetchType.LAZY)
    private List<CombateJuez> combateJueces = new ArrayList<>();
}
