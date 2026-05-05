package com.fabrica.soyla.validation;

import com.fabrica.soyla.model.InvitacionDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class InvitacionValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return InvitacionDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        InvitacionDTO dto = (InvitacionDTO) target;

        // Validar email destino
        if (dto.getEmailDestino() == null || dto.getEmailDestino().trim().isEmpty()) {
            errors.rejectValue("emailDestino", "emailDestino.empty", "El email del destinatario es obligatorio");
        } else if (!dto.getEmailDestino().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.rejectValue("emailDestino", "emailDestino.invalid", "El formato del email no es válido");
        }

        // Validar mensaje
        if (dto.getMensaje() != null && dto.getMensaje().length() > 500) {
            errors.rejectValue("mensaje", "mensaje.tooLong", "El mensaje no puede tener más de 500 caracteres");
        }
    }
}