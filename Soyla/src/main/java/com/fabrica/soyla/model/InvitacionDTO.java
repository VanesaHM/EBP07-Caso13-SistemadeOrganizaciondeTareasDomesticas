package com.fabrica.soyla.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InvitacionDTO {
    @NotBlank(message = "El email del destinatario es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String emailDestino;

    @NotNull(message = "El ID del grupo familiar es obligatorio")
    private Long grupoFamiliarId;

    private String mensaje;

    public InvitacionDTO() {}

    public InvitacionDTO(String emailDestino, Long grupoFamiliarId, String mensaje) {
        this.emailDestino = emailDestino;
        this.grupoFamiliarId = grupoFamiliarId;
        this.mensaje = mensaje;
    }

    public String getEmailDestino() { return emailDestino; }
    public void setEmailDestino(String emailDestino) { this.emailDestino = emailDestino; }

    public Long getGrupoFamiliarId() { return grupoFamiliarId; }
    public void setGrupoFamiliarId(Long grupoFamiliarId) { this.grupoFamiliarId = grupoFamiliarId; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}