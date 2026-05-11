package com.fabrica.soyla.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class InvitacionDTO {
    private Long id;

    @NotBlank(message = "El email del destinatario es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String emailDestino;

    @NotNull(message = "El ID del grupo familiar es obligatorio")
    private Long grupoFamiliarId;

    private String mensaje;
    private InvitacionGrupo.EstadoInvitacion estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaRespuesta;
    private GrupoFamiliarDTO grupo;
    private UsuarioDTO invitador;

    public InvitacionDTO() {}

    public InvitacionDTO(String emailDestino, Long grupoFamiliarId, String mensaje) {
        this.emailDestino = emailDestino;
        this.grupoFamiliarId = grupoFamiliarId;
        this.mensaje = mensaje;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmailDestino() { return emailDestino; }
    public void setEmailDestino(String emailDestino) { this.emailDestino = emailDestino; }

    public Long getGrupoFamiliarId() { return grupoFamiliarId; }
    public void setGrupoFamiliarId(Long grupoFamiliarId) { this.grupoFamiliarId = grupoFamiliarId; }
    public void setGrupoId(Long grupoId) { this.grupoFamiliarId = grupoId; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public InvitacionGrupo.EstadoInvitacion getEstado() { return estado; }
    public void setEstado(InvitacionGrupo.EstadoInvitacion estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }

    public GrupoFamiliarDTO getGrupo() { return grupo; }
    public void setGrupo(GrupoFamiliarDTO grupo) { this.grupo = grupo; }

    public UsuarioDTO getInvitador() { return invitador; }
    public void setInvitador(UsuarioDTO invitador) { this.invitador = invitador; }
}
