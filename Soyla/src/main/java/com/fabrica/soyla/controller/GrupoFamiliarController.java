package com.fabrica.soyla.controller;

import com.fabrica.soyla.config.JwtUtil;
import com.fabrica.soyla.model.*;
import com.fabrica.soyla.service.AuthService;
import com.fabrica.soyla.service.GrupoFamiliarService;
import com.fabrica.soyla.service.UsuarioService;
import com.fabrica.soyla.validation.GrupoFamiliarValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoFamiliarController {

    @Autowired
    private GrupoFamiliarService grupoFamiliarService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthService authService;

    @Autowired
    private GrupoFamiliarValidator grupoFamiliarValidator;

    @PostMapping
    public ResponseEntity<GrupoFamiliarDTO> crearGrupo(@RequestHeader("Authorization") String token,
                                                      @Valid @RequestBody CrearGrupoDTO crearGrupoDTO,
                                                      BindingResult result) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            grupoFamiliarValidator.validate(crearGrupoDTO, result);
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().build();
            }

            GrupoFamiliar grupo = grupoFamiliarService.crearGrupo(
                crearGrupoDTO.getNombre(),
                crearGrupoDTO.getDescripcion(),
                usuario
            );

            GrupoFamiliarDTO dto = convertirAGrupoFamiliarDTO(grupo);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<GrupoFamiliarDTO>> obtenerMisGrupos(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<GrupoFamiliar> grupos = grupoFamiliarService.obtenerGruposDeUsuario(usuario);
            List<GrupoFamiliarDTO> dtos = grupos.stream()
                .map(this::convertirAGrupoFamiliarDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoFamiliarDTO> obtenerGrupo(@RequestHeader("Authorization") String token,
                                                        @PathVariable Long id) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            GrupoFamiliar grupo = grupoFamiliarService.obtenerGrupoPorId(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

            if (!grupoFamiliarService.esMiembroActivo(grupo, usuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            GrupoFamiliarDTO dto = convertirAGrupoFamiliarDTO(grupo);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/miembros")
    public ResponseEntity<List<UsuarioDTO>> obtenerMiembros(@RequestHeader("Authorization") String token,
                                                           @PathVariable Long id) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            GrupoFamiliar grupo = grupoFamiliarService.obtenerGrupoPorId(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

            if (!grupoFamiliarService.esMiembroActivo(grupo, usuario)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<GrupoMiembro> miembros = grupoFamiliarService.obtenerMiembrosActivos(grupo);
            List<UsuarioDTO> dtos = miembros.stream()
                .map(miembro -> {
                    UsuarioDTO dto = new UsuarioDTO();
                    dto.setId(miembro.getUsuario().getId());
                    dto.setNombre(miembro.getUsuario().getNombre());
                    dto.setEmail(miembro.getUsuario().getEmail());
                    return dto;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private GrupoFamiliarDTO convertirAGrupoFamiliarDTO(GrupoFamiliar grupo) {
        GrupoFamiliarDTO dto = new GrupoFamiliarDTO();
        dto.setId(grupo.getId());
        dto.setNombre(grupo.getNombre());
        dto.setDescripcion(grupo.getDescripcion());
        dto.setFechaCreacion(grupo.getFechaCreacion());

        UsuarioDTO creadorDTO = new UsuarioDTO();
        creadorDTO.setId(grupo.getCreador().getId());
        creadorDTO.setNombre(grupo.getCreador().getNombre());
        creadorDTO.setEmail(grupo.getCreador().getEmail());
        dto.setCreador(creadorDTO);

        return dto;
    }
}
