package com.app.microservicio.usuarios.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class AuthenticationRequestDTO {
    private String username;
    private String contrasena;


    public AuthenticationRequestDTO(String username, String password) {
        this.username = username;
        this.contrasena = password;
    }
}
