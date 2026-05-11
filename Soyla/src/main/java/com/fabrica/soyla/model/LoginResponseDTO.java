package com.fabrica.soyla.model;

public class LoginResponseDTO {
    private String token;
    private Usuario usuario;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public LoginResponseDTO(String token, Long id, String nombre, String email) {
        this.token = token;
        this.usuario = new Usuario();
        this.usuario.setId(id);
        this.usuario.setNombre(nombre);
        this.usuario.setEmail(email);
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
