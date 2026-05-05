package com.fabrica.soyla.validation;

import com.fabrica.soyla.model.EditarPerfilDTO;
import com.fabrica.soyla.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UsuarioValidator implements Validator {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return EditarPerfilDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EditarPerfilDTO dto = (EditarPerfilDTO) target;

        // Validar nombre
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "nombre.empty", "El nombre es obligatorio");
        } else if (dto.getNombre().length() < 2) {
            errors.rejectValue("nombre", "nombre.tooShort", "El nombre debe tener al menos 2 caracteres");
        } else if (dto.getNombre().length() > 50) {
            errors.rejectValue("nombre", "nombre.tooLong", "El nombre no puede tener más de 50 caracteres");
        }

        // Validar email
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "email.empty", "El email es obligatorio");
        } else if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.rejectValue("email", "email.invalid", "El formato del email no es válido");
        } else {
            // Verificar si el email ya está en uso por otro usuario
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(usuario -> {
                if (!usuario.getId().equals(dto.getUsuarioId())) {
                    errors.rejectValue("email", "email.duplicate", "Este email ya está registrado");
                }
            });
        }
    }
}