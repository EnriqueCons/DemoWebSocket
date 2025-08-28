package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "CombateJuez")
public class CombateJuez implements Serializable {

    @EmbeddedId
    private CombateJuezId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("idCombate")
    @JoinColumn(name = "idCombate",
            foreignKey = @ForeignKey(name = "fk_cj_combate"))
    private Combate combate;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("idJuez")
    @JoinColumn(name = "idJuez",
            foreignKey = @ForeignKey(name = "fk_cj_juez"))
    private Juez juez;

    @Column(name = "rolJuez", length = 50)
    private String rolJuez;
}
