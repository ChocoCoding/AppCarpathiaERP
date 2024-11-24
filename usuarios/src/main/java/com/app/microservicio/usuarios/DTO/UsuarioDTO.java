package com.app.microservicio.usuarios.DTO;

import lombok.Data;

import java.util.Set;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombreUsuario;
    private String contrasena;
    private String email;
    private String nombreCompleto;
    private boolean enabled;
    private Set<String> roles;
}