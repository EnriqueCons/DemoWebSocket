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
@Table(name = "Administrador")
public class Administrador implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAdministrador", nullable = false)
    private Integer idAdministrador;

    @Column(name = "nombreAdministrador", length = 100)
    private String nombreAdministrador;

    @Column(name = "paternoAdministrador", length = 100)
    private String paternoAdministrador;

    @Column(name = "maternoAdministrador", length = 100)
    private String maternoAdministrador;

    @Column(name = "correoAdministrador", length = 100)
    private String correoAdministrador;

    @Column(name = "contraseniaAdministrador", length = 255)
    private String contraseniaAdministrador;

    @OneToMany(mappedBy = "administrador", fetch = FetchType.LAZY)
    private List<Torneo> torneos = new ArrayList<>();
}
