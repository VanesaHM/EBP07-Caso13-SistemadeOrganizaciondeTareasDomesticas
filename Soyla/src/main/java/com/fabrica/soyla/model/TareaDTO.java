package com.fabrica.soyla.model;

import com.fabrica.soyla.model.Tarea.EstadoTarea;
import com.fabrica.soyla.model.Tarea.PrioridadTarea;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TareaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private EstadoTarea estado;
    private PrioridadTarea prioridad;
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCompletada;
    private UsuarioDTO creador;
    private UsuarioDTO asignadoA;
    private GrupoFamiliarDTO grupoFamiliar;

    public TareaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoTarea getEstado() { return estado; }
    public void setEstado(EstadoTarea estado) { this.estado = estado; }

    public PrioridadTarea getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadTarea prioridad) { this.prioridad = prioridad; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaCompletada() { return fechaCompletada; }
    public void setFechaCompletada(LocalDateTime fechaCompletada) { this.fechaCompletada = fechaCompletada; }

    public UsuarioDTO getCreador() { return creador; }
    public void setCreador(UsuarioDTO creador) { this.creador = creador; }

    public UsuarioDTO getAsignadoA() { return asignadoA; }
    public void setAsignadoA(UsuarioDTO asignadoA) { this.asignadoA = asignadoA; }

    public GrupoFamiliarDTO getGrupoFamiliar() { return grupoFamiliar; }
    public void setGrupoFamiliar(GrupoFamiliarDTO grupoFamiliar) { this.grupoFamiliar = grupoFamiliar; }
}