package com.fabrica.soyla.validation;

import com.fabrica.soyla.model.CrearGrupoDTO;
import com.fabrica.soyla.repository.GrupoFamiliarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GrupoFamiliarValidator implements Validator {

    @Autowired
    private GrupoFamiliarRepository grupoFamiliarRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return CrearGrupoDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CrearGrupoDTO dto = (CrearGrupoDTO) target;

        // Validar nombre
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "nombre.empty", "El nombre del grupo es obligatorio");
        } else if (dto.getNombre().length() < 3) {
            errors.rejectValue("nombre", "nombre.tooShort", "El nombre del grupo debe tener al menos 3 caracteres");
        } else if (dto.getNombre().length() > 50) {
            errors.rejectValue("nombre", "nombre.tooLong", "El nombre del grupo no puede tener más de 50 caracteres");
        }

        // Validar descripción
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 200) {
            errors.rejectValue("descripcion", "descripcion.tooLong", "La descripción no puede tener más de 200 caracteres");
        }
    }
}