package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombateJuezId implements Serializable {
    @Column(name = "idCombate", nullable = false)
    private Integer idCombate;

    @Column(name = "idJuez", nullable = false)
    private Integer idJuez;
}
