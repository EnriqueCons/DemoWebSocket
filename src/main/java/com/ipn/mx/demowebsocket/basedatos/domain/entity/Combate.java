package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.*;
import java.util.*;

@Getter
@Setter
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

    // ... (otros campos se quedan igual)
    @Column(name = "numeroRound")
    private Integer numeroRound;

    @Column(name = "duracionRound")
    private LocalTime duracionRound;

    @Column(name = "duracionDescanso")
    private LocalTime duracionDescanso;

    @Column(name = "horaCombate")
    private LocalDateTime horaCombate;

    @Column(name = "contrasenaCombate", length = 255)
    private String contrasenaCombate;

    @Column(name = "estado", length = 50)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAreaCombate",
            foreignKey = @ForeignKey(name = "fk_combate_area"))
    private AreaCombate areaCombate;

    // --- CORRECCIONES ---
    @OneToMany(mappedBy = "combate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("combate-puntajes")
    private List<PuntajeDetalle> puntajes = new ArrayList<>();

    @OneToMany(mappedBy = "combate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("combate-participaciones")
    private List<Participacion> participaciones = new ArrayList<>();

    @OneToMany(mappedBy = "combate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("combate-jueces")
    private List<CombateJuez> combateJueces = new ArrayList<>();
}