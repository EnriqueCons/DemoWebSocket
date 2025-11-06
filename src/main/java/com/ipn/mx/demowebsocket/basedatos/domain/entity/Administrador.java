package com.ipn.mx.demowebsocket.basedatos.domain.entity;

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
@Table(
        name = "Administrador",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_admin_correo", columnNames = "correoAdministrador"),
                @UniqueConstraint(name="uk_admin_usuario", columnNames = "usuarioAdministrador")
        }
)
public class Administrador implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAdministrador", nullable = false)
    private Integer idAdministrador;

    @Column(name = "nombreAdministrador", length = 100, nullable = false)
    private String nombreAdministrador;

    @Column(name = "paternoAdministrador", length = 100, nullable = false)
    private String paternoAdministrador;

    @Column(name = "maternoAdministrador", length = 100)
    private String maternoAdministrador;

    @Column(name = "usuarioAdministrador", length = 100, nullable = false)
    private String usuarioAdministrador;

    @Column(name = "correoAdministrador", length = 100, nullable = false)
    private String correoAdministrador;

    @Column(name = "contraseniaAdministrador", length = 255, nullable = false)
    private String contraseniaAdministrador;

    @JsonIgnore
    @OneToMany(mappedBy = "administrador", fetch = FetchType.LAZY)
    private List<Torneo> torneos = new ArrayList<>();
}
