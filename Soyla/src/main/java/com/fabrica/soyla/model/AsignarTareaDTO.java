package com.fabrica.soyla.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AsignarTareaDTO {
    @NotBlank(message = "El email del usuario asignado es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String assignedToEmail;

    public AsignarTareaDTO() {}

    public AsignarTareaDTO(String assignedToEmail) {
        this.assignedToEmail = assignedToEmail;
    }

    public String getAssignedToEmail() { return assignedToEmail; }
    public void setAssignedToEmail(String assignedToEmail) { this.assignedToEmail = assignedToEmail; }
}