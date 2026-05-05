package com.fabrica.soyla.service;

import com.fabrica.soyla.model.GrupoFamiliar;
import com.fabrica.soyla.model.InvitacionGrupo;
import com.fabrica.soyla.model.Usuario;
import com.fabrica.soyla.repository.InvitacionGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvitacionService {

    @Autowired
    private InvitacionGrupoRepository invitacionRepository;

    @Autowired
    private GrupoFamiliarService grupoFamiliarService;

    @Autowired
    private UsuarioService usuarioService;

    public InvitacionGrupo enviarInvitacion(String emailDestino, GrupoFamiliar grupo,
                                          Usuario invitador, String mensaje) {

        if (!grupoFamiliarService.esMiembroActivo(grupo, invitador)) {
            throw new RuntimeException("No tienes permisos para invitar a este grupo");
        }

        // Verificar si ya existe una invitación pendiente
        if (invitacionRepository.existsByEmailDestinoAndGrupoFamiliarAndEstado(
                emailDestino, grupo, InvitacionGrupo.EstadoInvitacion.PENDIENTE)) {
            throw new RuntimeException("Ya existe una invitación pendiente para este email");
        }

        // Verificar si el usuario ya es miembro
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorEmail(emailDestino);
        if (usuarioExistente.isPresent() &&
            grupoFamiliarService.esMiembroActivo(grupo, usuarioExistente.get())) {
            throw new RuntimeException("El usuario ya es miembro del grupo");
        }

        InvitacionGrupo invitacion = new InvitacionGrupo(emailDestino, grupo, invitador, mensaje);
        return invitacionRepository.save(invitacion);
    }

    public List<InvitacionGrupo> obtenerInvitacionesPendientes(String email) {
        return invitacionRepository.findByEmailDestinoAndEstado(email, InvitacionGrupo.EstadoInvitacion.PENDIENTE);
    }

    public void aceptarInvitacion(Long invitacionId, Usuario usuario) {
        Optional<InvitacionGrupo> invitacionOpt = invitacionRepository.findById(invitacionId);
        if (invitacionOpt.isEmpty()) {
            throw new RuntimeException("Invitación no encontrada");
        }

        InvitacionGrupo invitacion = invitacionOpt.get();

        if (!invitacion.getEmailDestino().equals(usuario.getEmail())) {
            throw new RuntimeException("Esta invitación no es para ti");
        }

        if (invitacion.getEstado() != InvitacionGrupo.EstadoInvitacion.PENDIENTE) {
            throw new RuntimeException("La invitación ya fue procesada");
        }

        invitacion.setEstado(InvitacionGrupo.EstadoInvitacion.ACEPTADA);
        invitacionRepository.save(invitacion);

        // Agregar al usuario al grupo
        grupoFamiliarService.agregarMiembro(invitacion.getGrupoFamiliar(), usuario);
    }

    public void rechazarInvitacion(Long invitacionId, Usuario usuario) {
        Optional<InvitacionGrupo> invitacionOpt = invitacionRepository.findById(invitacionId);
        if (invitacionOpt.isEmpty()) {
            throw new RuntimeException("Invitación no encontrada");
        }

        InvitacionGrupo invitacion = invitacionOpt.get();

        if (!invitacion.getEmailDestino().equals(usuario.getEmail())) {
            throw new RuntimeException("Esta invitación no es para ti");
        }

        if (invitacion.getEstado() != InvitacionGrupo.EstadoInvitacion.PENDIENTE) {
            throw new RuntimeException("La invitación ya fue procesada");
        }

        invitacion.setEstado(InvitacionGrupo.EstadoInvitacion.RECHAZADA);
        invitacionRepository.save(invitacion);
    }

    public List<InvitacionGrupo> obtenerInvitacionesDeGrupo(GrupoFamiliar grupo) {
        return invitacionRepository.findByGrupoFamiliarAndEstado(grupo, InvitacionGrupo.EstadoInvitacion.PENDIENTE);
    }
}