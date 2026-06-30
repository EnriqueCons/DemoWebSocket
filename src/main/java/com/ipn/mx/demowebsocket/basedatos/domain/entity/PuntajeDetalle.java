package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PuntajeDetalle")
public class PuntajeDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPuntaje")
    private Integer idPuntaje;

    @Column(name = "valorPuntaje")
    private Integer valorPuntaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCombate")
    @JsonBackReference("combate-puntajes")
    private Combate combate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAlumno")
    @JsonBackReference("alumno-puntajes")
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTipoPuntaje")
    private TipoPuntaje tipoPuntaje;

    @Column(name = "roundNumero", columnDefinition = "INT DEFAULT 1")
    private Integer roundNumero = 1;

    @Column(name = "fechaRegistro", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.roundNumero == null) {
            this.roundNumero = 1;
        }
    }
}
