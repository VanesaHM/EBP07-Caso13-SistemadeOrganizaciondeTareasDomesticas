package com.fabrica.soyla.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitaciones_grupo")
public class InvitacionGrupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(name = "email_destino")
    private String emailDestino;

    @ManyToOne
    @JoinColumn(name = "grupo_familiar_id", nullable = false)
    private GrupoFamiliar grupoFamiliar;

    @ManyToOne
    @JoinColumn(name = "invitador_id", nullable = false)
    private Usuario invitador;

    @Enumerated(EnumType.STRING)
    private EstadoInvitacion estado = EstadoInvitacion.PENDIENTE;

    private String mensaje;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    public enum EstadoInvitacion {
        PENDIENTE, ACEPTADA, RECHAZADA, EXPIRADA
    }

    public InvitacionGrupo() {}

    public InvitacionGrupo(String emailDestino, GrupoFamiliar grupoFamiliar,
                          Usuario invitador, String mensaje) {
        this.emailDestino = emailDestino;
        this.grupoFamiliar = grupoFamiliar;
        this.invitador = invitador;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmailDestino() { return emailDestino; }
    public void setEmailDestino(String emailDestino) { this.emailDestino = emailDestino; }

    public GrupoFamiliar getGrupoFamiliar() { return grupoFamiliar; }
    public void setGrupoFamiliar(GrupoFamiliar grupoFamiliar) { this.grupoFamiliar = grupoFamiliar; }

    public Usuario getInvitador() { return invitador; }
    public void setInvitador(Usuario invitador) { this.invitador = invitador; }

    public EstadoInvitacion getEstado() { return estado; }
    public void setEstado(EstadoInvitacion estado) { this.estado = estado; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
}