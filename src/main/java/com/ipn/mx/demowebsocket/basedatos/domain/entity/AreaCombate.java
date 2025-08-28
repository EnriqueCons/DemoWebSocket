package com.ipn.mx.demowebsocket.basedatos.domain.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTorneo",
            foreignKey = @ForeignKey(name = "fk_area_torneo"))
    private Torneo torneo;

    @OneToMany(mappedBy = "areaCombate", fetch = FetchType.LAZY)
    private List<Peto> petos = new ArrayList<>();

    @OneToMany(mappedBy = "areaCombate", fetch = FetchType.LAZY)
    private List<Combate> combates = new ArrayList<>();
}
