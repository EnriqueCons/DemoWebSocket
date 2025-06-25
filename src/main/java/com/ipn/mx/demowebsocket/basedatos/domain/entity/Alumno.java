package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Alumno")
public class Alumno implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAlumno", nullable = false)
    private Integer idAlumno;

    @Column(name = "nombreAlumno", nullable = false, length = 50)
    private String nombreAlumno;

    @Column(name = "paternoAlumno", nullable = false, length = 50)
    private String paternoAlumno;

    @Column(name = "maternoAlumno", nullable = false, length = 50)
    private String maternoAlumno;

    @Column(name = "sexo", nullable = false, length = 10)
    private String sexo;

    @Column(name = "fechaNacimiento", nullable = false)
    private Date fechaNacimiento;

    @Column(name = "peso", nullable = false)
    private BigDecimal peso;

    @OneToOne
    @JoinColumn(name = "idPeto", nullable = false)
    private Peto peto;


}
