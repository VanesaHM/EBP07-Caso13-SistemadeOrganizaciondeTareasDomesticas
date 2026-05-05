package com.fabrica.soyla.model;

import com.fabrica.soyla.model.Tarea.EstadoTarea;

public class CambiarEstadoTareaDTO {
    private EstadoTarea estado;

    public CambiarEstadoTareaDTO() {}

    public EstadoTarea getEstado() { return estado; }
    public void setEstado(EstadoTarea estado) { this.estado = estado; }
}