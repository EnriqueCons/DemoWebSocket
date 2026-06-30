package com.ipn.mx.demowebsocket.basedatos.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tipo_puntaje")
public class TipoPuntaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoPuntaje;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull
    @Column(nullable = false)
    private Integer valor;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean modoRapido = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo = true;

    // Getters y Setters

    public Integer getIdTipoPuntaje() { return idTipoPuntaje; }
    public void setIdTipoPuntaje(Integer idTipoPuntaje) { this.idTipoPuntaje = idTipoPuntaje; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getValor() { return valor; }
    public void setValor(Integer valor) { this.valor = valor; }

    public Boolean getModoRapido() { return modoRapido; }
    public void setModoRapido(Boolean modoRapido) { this.modoRapido = modoRapido; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
