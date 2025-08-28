package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity @Table(name = "Torneo")
public class Torneo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTorneo", nullable = false)
    private Integer idTorneo;

    @Column(name = "fechaHora")
    private LocalDateTime fechaHora;

    @Column(name = "sede", length = 100)
    private String sede;

    @Column(name = "estado", length = 50)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAdministrador",
            foreignKey = @ForeignKey(name = "fk_torneo_admin"))
    private Administrador administrador;

    @OneToMany(mappedBy = "torneo", fetch = FetchType.LAZY)
    private List<AreaCombate> areas = new ArrayList<>();
}
