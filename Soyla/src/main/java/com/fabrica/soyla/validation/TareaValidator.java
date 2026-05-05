package com.fabrica.soyla.validation;

import com.fabrica.soyla.model.CrearTareaDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class TareaValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CrearTareaDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CrearTareaDTO dto = (CrearTareaDTO) target;

        // Validar nombre
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            errors.rejectValue("nombre", "nombre.empty", "El nombre de la tarea es obligatorio");
        } else if (dto.getNombre().length() < 3) {
            errors.rejectValue("nombre", "nombre.tooShort", "El nombre de la tarea debe tener al menos 3 caracteres");
        } else if (dto.getNombre().length() > 200) {
            errors.rejectValue("nombre", "nombre.tooLong", "El nombre de la tarea no puede tener más de 200 caracteres");
        }

        // Validar descripción
        if (dto.getDescripcion() != null && dto.getDescripcion().length() > 1000) {
            errors.rejectValue("descripcion", "descripcion.tooLong", "La descripción no puede tener más de 1000 caracteres");
        }

        // Validar fecha de vencimiento
        if (dto.getFechaVencimiento() != null && dto.getFechaVencimiento().isBefore(LocalDate.now())) {
            errors.rejectValue("fechaVencimiento", "fechaVencimiento.past", "La fecha de vencimiento no puede ser anterior a hoy");
        }

        // Validar prioridad
        if (dto.getPrioridad() == null) {
            errors.rejectValue("prioridad", "prioridad.empty", "La prioridad es obligatoria");
        }
    }
}