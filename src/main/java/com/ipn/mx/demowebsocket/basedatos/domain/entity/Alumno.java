package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Importante
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

    // ... otros campos como nombre, peso, etc.
    private String nombreAlumno;
    private String paternoAlumno;
    private String maternoAlumno;
    private LocalDate fechaNacimiento;
    private String sexo;
    private BigDecimal peso;

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