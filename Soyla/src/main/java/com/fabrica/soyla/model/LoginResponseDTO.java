package com.fabrica.soyla.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {
    private String token;
    private String mensaje;
    private String usuario;
    private String fullName;
    private String email;

    public LoginResponseDTO(String token, String usuario, String fullName, String email) {
        this.token = token;
        this.usuario = usuario;
        this.fullName = fullName;
        this.email = email;
        this.mensaje = "Inicio de sesion exitoso";
    }
}
