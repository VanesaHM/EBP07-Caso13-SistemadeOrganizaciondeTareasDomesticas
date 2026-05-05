package com.fabrica.soyla.controller;

import com.fabrica.soyla.model.*;
import com.fabrica.soyla.service.*;
import com.fabrica.soyla.validation.TareaValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tareas")
@CrossOrigin(origins = "*")
public class TareaController {

    @Autowired
    private TareaService tareaService;

    @Autowired
    private GrupoFamiliarService grupoFamiliarService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthService authService;

    @Autowired
    private TareaValidator tareaValidator;

    @PostMapping("/grupos/{grupoId}")
    public ResponseEntity<TareaDTO> crearTarea(@RequestHeader("Authorization") String token,
                                              @PathVariable Long grupoId,
                                              @Valid @RequestBody CrearTareaDTO crearTareaDTO,
                                              BindingResult result) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            GrupoFamiliar grupo = grupoFamiliarService.obtenerGrupoPorId(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

            tareaValidator.validate(crearTareaDTO, result);
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().build();
            }

            Usuario asignadoA = null;
            if (crearTareaDTO.getAsignadoAId() != null) {
                asignadoA = usuarioService.buscarPorId(crearTareaDTO.getAsignadoAId())
                    .orElseThrow(() -> new RuntimeException("Usuario asignado no encontrado"));
            }

            Tarea tarea = tareaService.crearTarea(
                crearTareaDTO.getNombre(),
                crearTareaDTO.getDescripcion(),
                grupo,
                usuario,
                asignadoA,
                crearTareaDTO.getFechaVencimiento(),
                crearTareaDTO.getPrioridad()
            );

            TareaDTO dto = convertirATareaDTO(tarea);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/grupos/{grupoId}")
    public ResponseEntity<List<TareaDTO>> obtenerTareasDeGrupo(@RequestHeader("Authorization") String token,
                                                              @PathVariable Long grupoId) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            GrupoFamiliar grupo = grupoFamiliarService.obtenerGrupoPorId(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

            List<Tarea> tareas = tareaService.obtenerTareasDeGrupo(grupo, usuario);
            List<TareaDTO> dtos = tareas.stream()
                .map(this::convertirATareaDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/mis-tareas")
    public ResponseEntity<List<TareaDTO>> obtenerMisTareas(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Tarea> tareas = tareaService.obtenerTareasAsignadasAUsuario(usuario);
            List<TareaDTO> dtos = tareas.stream()
                .map(this::convertirATareaDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstadoTarea(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long id,
                                                  @RequestBody CambiarEstadoTareaDTO cambiarEstadoDTO) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            tareaService.cambiarEstadoTarea(id, cambiarEstadoDTO.getEstado(), usuario);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/asignar")
    public ResponseEntity<Void> asignarTarea(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id,
                                            @RequestBody AsignarTareaDTO asignarTareaDTO) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuarioActual = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Usuario asignadoA = null;
            if (asignarTareaDTO.getUsuarioId() != null) {
                asignadoA = usuarioService.buscarPorId(asignarTareaDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            }

            tareaService.asignarTarea(id, asignadoA, usuarioActual);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarea(@RequestHeader("Authorization") String token,
                                             @PathVariable Long id) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            tareaService.eliminarTarea(id, usuario);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private TareaDTO convertirATareaDTO(Tarea tarea) {
        TareaDTO dto = new TareaDTO();
        dto.setId(tarea.getId());
        dto.setNombre(tarea.getNombre());
        dto.setDescripcion(tarea.getDescripcion());
        dto.setEstado(tarea.getEstado());
        dto.setPrioridad(tarea.getPrioridad());
        dto.setFechaVencimiento(tarea.getFechaVencimiento());
        dto.setFechaCreacion(tarea.getFechaCreacion());
        dto.setFechaCompletada(tarea.getFechaCompletada());

        UsuarioDTO creadorDTO = new UsuarioDTO();
        creadorDTO.setId(tarea.getCreador().getId());
        creadorDTO.setNombre(tarea.getCreador().getNombre());
        creadorDTO.setEmail(tarea.getCreador().getEmail());
        dto.setCreador(creadorDTO);

        if (tarea.getAsignadoA() != null) {
            UsuarioDTO asignadoDTO = new UsuarioDTO();
            asignadoDTO.setId(tarea.getAsignadoA().getId());
            asignadoDTO.setNombre(tarea.getAsignadoA().getNombre());
            asignadoDTO.setEmail(tarea.getAsignadoA().getEmail());
            dto.setAsignadoA(asignadoDTO);
        }

        GrupoFamiliarDTO grupoDTO = new GrupoFamiliarDTO();
        grupoDTO.setId(tarea.getGrupoFamiliar().getId());
        grupoDTO.setNombre(tarea.getGrupoFamiliar().getNombre());
        dto.setGrupoFamiliar(grupoDTO);

        return dto;
    }
}