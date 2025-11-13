package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "CombateJuez",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_combatejuez_combate_juez", columnNames = {"idCombate","idJuez"}),
                @UniqueConstraint(name = "uk_combatejuez_combate_rol",  columnNames = {"idCombate","rol"})
        }
)
public class CombateJuez implements Serializable {

    @EmbeddedId
    private CombateJuezId id; // (idCombate, idJuez)

    @MapsId("idCombate")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCombate", nullable = false)
    private Combate combate;

    @MapsId("idJuez")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idJuez", nullable = false)
    private Juez juez;

    @Column(name = "rol", length = 20, nullable = false) // CENTRAL, J1, J2, J3
    private String rol;
}
