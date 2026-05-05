package com.fabrica.soyla.model;

import java.time.LocalDateTime;

public class GrupoFamiliarDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private UsuarioDTO creador;

    public GrupoFamiliarDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public UsuarioDTO getCreador() { return creador; }
    public void setCreador(UsuarioDTO creador) { this.creador = creador; }
}