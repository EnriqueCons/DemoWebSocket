package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCategoria")
    private Integer idCategoria;

    @Column(name = "nombreCategoria", length = 100)
    private String nombreCategoria;

    @Column(name = "clasificacion", length = 50)
    private String clasificacion;

    @Column(name = "pesoMinimo", precision = 5, scale = 2)
    private BigDecimal pesoMinimo;

    @Column(name = "pesoMaximo", precision = 5, scale = 2)
    private BigDecimal pesoMaximo;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Alumno> alumnos = new ArrayList<>();
}