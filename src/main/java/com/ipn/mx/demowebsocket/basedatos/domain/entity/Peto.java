package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Peto")
public class Peto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPeto", nullable = false)
    private Integer idPeto;

    @Column(name = "numeroPeto", length = 10)
    private String numeroPeto;

    // Relación con AreaCombate (padre) - Evita ciclos JSON
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAreaCombate",
            foreignKey = @ForeignKey(name = "fk_peto_area"))
    @JsonBackReference("area-petos")
    private AreaCombate areaCombate;
}