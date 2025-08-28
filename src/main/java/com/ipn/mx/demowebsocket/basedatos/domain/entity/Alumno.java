package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity @Table(name = "Alumno")
public class Alumno implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAlumno", nullable = false)
    private Integer idAlumno;

    @Column(name = "nombreAlumno", length = 100)
    private String nombreAlumno;

    @Column(name = "paternoAlumno", length = 100)
    private String paternoAlumno;

    @Column(name = "maternoAlumno", length = 100)
    private String maternoAlumno;

    @Column(name = "fechaNacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "sexo", length = 10)
    private String sexo;

    @Column(name = "peso", precision = 5, scale = 2)
    private BigDecimal peso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCategoria",
            foreignKey = @ForeignKey(name = "fk_alumno_categoria"))
    private Categoria categoria;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<PuntajeDetalle> puntajes = new ArrayList<>();

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<Participacion> participaciones = new ArrayList<>();
}
