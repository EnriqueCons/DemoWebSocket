package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Alumno")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAlumno")
    private Long idAlumno;

    @Column(name = "nombreAlumno", length = 100, nullable = false)
    private String nombreAlumno;

    @Column(name = "paternoAlumno", length = 100, nullable = true)
    private String paternoAlumno;

    @Column(name = "maternoAlumno", length = 100, nullable = true)
    private String maternoAlumno;

    @Column(name = "fechaNacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "sexo", length = 20, nullable = false)
    private String sexo;

    @Column(name = "peso", nullable = false)
    private BigDecimal peso;

    @Column(name = "altura", nullable = false)
    private BigDecimal altura;

    @Column(name = "nacionalidad", length = 50, nullable = false)
    private String nacionalidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;

    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    @JsonManagedReference("alumno-participaciones")
    private List<Participacion> participaciones = new ArrayList<>();

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("alumno-puntajes")
    private List<PuntajeDetalle> puntajes = new ArrayList<>();
}