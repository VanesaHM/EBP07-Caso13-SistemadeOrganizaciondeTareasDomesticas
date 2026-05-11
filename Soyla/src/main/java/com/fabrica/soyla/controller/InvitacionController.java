package com.fabrica.soyla.controller;

import com.fabrica.soyla.model.*;
import com.fabrica.soyla.service.*;
import com.fabrica.soyla.validation.InvitacionValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/invitaciones")
@CrossOrigin(origins = "*")
public class InvitacionController {

    @Autowired
    private InvitacionService invitacionService;

    @Autowired
    private GrupoFamiliarService grupoFamiliarService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthService authService;

    @Autowired
    private InvitacionValidator invitacionValidator;

    @PostMapping("/grupos/{grupoId}")
    public ResponseEntity<InvitacionDTO> enviarInvitacion(@RequestHeader("Authorization") String token,
                                                         @PathVariable Long grupoId,
                                                         @Valid @RequestBody InvitacionDTO invitacionDTO,
                                                         BindingResult result) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            GrupoFamiliar grupo = grupoFamiliarService.obtenerGrupoPorId(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

            invitacionDTO.setGrupoId(grupoId);
            invitacionValidator.validate(invitacionDTO, result);
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().build();
            }

            InvitacionGrupo invitacion = invitacionService.enviarInvitacion(
                invitacionDTO.getEmailDestino(),
                grupo,
                usuario,
                invitacionDTO.getMensaje()
            );

            InvitacionDTO dto = convertirAInvitacionDTO(invitacion);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<InvitacionDTO>> obtenerMisInvitaciones(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));

            List<InvitacionGrupo> invitaciones = invitacionService.obtenerInvitacionesPendientes(email);
            List<InvitacionDTO> dtos = invitaciones.stream()
                .map(this::convertirAInvitacionDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/aceptar")
    public ResponseEntity<Void> aceptarInvitacion(@RequestHeader("Authorization") String token,
                                                 @PathVariable Long id) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            invitacionService.aceptarInvitacion(id, usuario);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<Void> rechazarInvitacion(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long id) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            invitacionService.rechazarInvitacion(id, usuario);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/grupos/{grupoId}")
    public ResponseEntity<List<InvitacionDTO>> obtenerInvitacionesDeGrupo(@RequestHeader("Authorization") String token,
                                                                         @PathVariable Long grupoId) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            GrupoFamiliar grupo = grupoFamiliarService.obtenerGrupoPorId(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

            if (!grupoFamiliarService.esCreador(grupo, usuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<InvitacionGrupo> invitaciones = invitacionService.obtenerInvitacionesDeGrupo(grupo);
            List<InvitacionDTO> dtos = invitaciones.stream()
                .map(this::convertirAInvitacionDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private InvitacionDTO convertirAInvitacionDTO(InvitacionGrupo invitacion) {
        InvitacionDTO dto = new InvitacionDTO();
        dto.setId(invitacion.getId());
        dto.setEmailDestino(invitacion.getEmailDestino());
        dto.setMensaje(invitacion.getMensaje());
        dto.setEstado(invitacion.getEstado());
        dto.setFechaCreacion(invitacion.getFechaCreacion());
        dto.setFechaRespuesta(invitacion.getFechaRespuesta());

        GrupoFamiliarDTO grupoDTO = new GrupoFamiliarDTO();
        grupoDTO.setId(invitacion.getGrupoFamiliar().getId());
        grupoDTO.setNombre(invitacion.getGrupoFamiliar().getNombre());
        dto.setGrupo(grupoDTO);

        UsuarioDTO invitadorDTO = new UsuarioDTO();
        invitadorDTO.setId(invitacion.getInvitador().getId());
        invitadorDTO.setNombre(invitacion.getInvitador().getNombre());
        invitadorDTO.setEmail(invitacion.getInvitador().getEmail());
        dto.setInvitador(invitadorDTO);

        return dto;
    }
}
