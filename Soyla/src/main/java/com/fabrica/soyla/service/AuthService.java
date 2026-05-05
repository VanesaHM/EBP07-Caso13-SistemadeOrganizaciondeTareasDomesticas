package com.fabrica.soyla.service;

import com.fabrica.soyla.config.JwtUtil;
import com.fabrica.soyla.model.LoginDTO;
import com.fabrica.soyla.model.LoginResponseDTO;
import com.fabrica.soyla.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginDTO loginDTO) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(loginDTO.getEmail());

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuarioService.validarPassword(loginDTO.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(usuario.getEmail());

        return new LoginResponseDTO(token, usuario.getId(), usuario.getNombre(), usuario.getEmail());
    }

    public boolean validarToken(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            return jwtUtil.validateToken(token, email);
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerEmailDeToken(String token) {
        return jwtUtil.extractUsername(token);
    }
}