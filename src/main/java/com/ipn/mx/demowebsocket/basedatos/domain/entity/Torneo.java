package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Torneo")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Torneo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTorneo", nullable = false)
    private Integer idTorneo;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "fechaHora")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHora;

    @Column(name = "sede", length = 100)
    private String sede;

    @Column(name = "estado", length = 50)
    private String estado;

    // Relación con Administrador (NO se elimina en cascada)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idAdministrador",
            foreignKey = @ForeignKey(name = "fk_torneo_admin"))
    private Administrador administrador;

    // Relación con Areas - SE ELIMINAN en cascada
    @OneToMany(
            mappedBy = "torneo",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference("torneo-areas")
    private List<AreaCombate> areas = new ArrayList<>();
}