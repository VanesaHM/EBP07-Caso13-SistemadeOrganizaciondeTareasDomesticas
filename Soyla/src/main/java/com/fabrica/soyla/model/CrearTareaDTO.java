package com.fabrica.soyla.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CrearTareaDTO {
    @NotBlank(message = "El nombre de la tarea es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El ID del grupo familiar es obligatorio")
    private Long grupoFamiliarId;

    private LocalDate fechaVencimiento;
    private String prioridad = "MEDIA";

    public CrearTareaDTO() {}

    public CrearTareaDTO(String nombre, String descripcion, Long grupoFamiliarId,
                        LocalDate fechaVencimiento, String prioridad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.grupoFamiliarId = grupoFamiliarId;
        this.fechaVencimiento = fechaVencimiento;
        this.prioridad = prioridad;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Long getGrupoFamiliarId() { return grupoFamiliarId; }
    public void setGrupoFamiliarId(Long grupoFamiliarId) { this.grupoFamiliarId = grupoFamiliarId; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
}