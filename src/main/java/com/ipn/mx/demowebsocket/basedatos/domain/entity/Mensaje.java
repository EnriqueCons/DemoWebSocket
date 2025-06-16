package com.ipn.mx.demowebsocket.basedatos.domain.entity;
/*
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
@Data
public class Mensaje implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Double valor;

  @Column(name = "fechaRegistro", nullable = false)
  private LocalDateTime fechaRegistro;

  @PrePersist
  protected void onCreate() {
    fechaRegistro = LocalDateTime.now();
  }
}
*/