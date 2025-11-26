package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "AreaCombate")
public class AreaCombate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAreaCombate", nullable = false)
    private Integer idAreaCombate;

    @Column(name = "nombreArea", length = 100)
    private String nombreArea;

    // Relación con Torneo (padre) - NO se elimina en cascada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTorneo",
            foreignKey = @ForeignKey(name = "fk_area_torneo"))
    @JsonBackReference("torneo-areas")
    private Torneo torneo;

    // Relación con Petos - SE ELIMINAN en cascada
    @OneToMany(
            mappedBy = "areaCombate",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Peto> petos = new ArrayList<>();

    // Relación con Combates - SE ELIMINAN en cascada
    @OneToMany(
            mappedBy = "areaCombate",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Combate> combates = new ArrayList<>();
}