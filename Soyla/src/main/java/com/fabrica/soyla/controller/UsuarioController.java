package com.fabrica.soyla.controller;

import com.fabrica.soyla.config.JwtUtil;
import com.fabrica.soyla.model.EditarPerfilDTO;
import com.fabrica.soyla.model.CambiarPasswordDTO;
import com.fabrica.soyla.model.Usuario;
import com.fabrica.soyla.model.UsuarioDTO;
import com.fabrica.soyla.service.AuthService;
import com.fabrica.soyla.service.UsuarioService;
import com.fabrica.soyla.validation.UsuarioValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioValidator usuarioValidator;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> obtenerPerfil(@RequestHeader("Authorization") String token) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setEmail(usuario.getEmail());

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioDTO> editarPerfil(@RequestHeader("Authorization") String token,
                                                  @Valid @RequestBody EditarPerfilDTO editarPerfilDTO,
                                                  BindingResult result) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            editarPerfilDTO.setUsuarioId(usuario.getId());
            usuarioValidator.validate(editarPerfilDTO, result);

            if (result.hasErrors()) {
                return ResponseEntity.badRequest().build();
            }

            usuario.setNombre(editarPerfilDTO.getNombre());
            usuario.setEmail(editarPerfilDTO.getEmail());

            Usuario usuarioActualizado = usuarioService.actualizarUsuario(usuario);

            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(usuarioActualizado.getId());
            dto.setNombre(usuarioActualizado.getNombre());
            dto.setEmail(usuarioActualizado.getEmail());

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/password")
    public ResponseEntity<Void> cambiarPassword(@RequestHeader("Authorization") String token,
                                               @Valid @RequestBody CambiarPasswordDTO cambiarPasswordDTO) {
        try {
            String email = authService.extraerEmailDeToken(token.substring(7));
            Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Aquí deberías validar la contraseña actual antes de cambiarla
            usuarioService.cambiarPassword(usuario.getId(), cambiarPasswordDTO.getNuevaPassword());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}