package com.fabrica.soyla.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grupos_miembros")
public class GrupoMiembro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "grupo_familiar_id", nullable = false)
    private GrupoFamiliar grupoFamiliar;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private EstadoMiembro estado = EstadoMiembro.ACTIVO;

    @Column(name = "fecha_union")
    private LocalDateTime fechaUnion = LocalDateTime.now();

    public enum EstadoMiembro {
        ACTIVO, INACTIVO
    }

    public GrupoMiembro() {}

    public GrupoMiembro(GrupoFamiliar grupoFamiliar, Usuario usuario) {
        this.grupoFamiliar = grupoFamiliar;
        this.usuario = usuario;
    }

    public GrupoMiembro(GrupoFamiliar grupoFamiliar, Usuario usuario, EstadoMiembro estado) {
        this.grupoFamiliar = grupoFamiliar;
        this.usuario = usuario;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GrupoFamiliar getGrupoFamiliar() { return grupoFamiliar; }
    public void setGrupoFamiliar(GrupoFamiliar grupoFamiliar) { this.grupoFamiliar = grupoFamiliar; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public EstadoMiembro getEstado() { return estado; }
    public void setEstado(EstadoMiembro estado) { this.estado = estado; }

    public LocalDateTime getFechaUnion() { return fechaUnion; }
    public void setFechaUnion(LocalDateTime fechaUnion) { this.fechaUnion = fechaUnion; }
}
