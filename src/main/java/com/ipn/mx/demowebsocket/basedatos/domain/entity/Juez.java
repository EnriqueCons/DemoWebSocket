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
@Table(name = "Juez")
public class Juez implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idJuez", nullable = false)
    private Integer idJuez;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "apellidoPaterno", length = 100)
    private String apellidoPaterno;

    @Column(name = "apellidoMaterno", length = 100)
    private String apellidoMaterno;

    @OneToMany(mappedBy = "juez", fetch = FetchType.LAZY)
    private List<CombateJuez> combateJueces = new ArrayList<>();
}
