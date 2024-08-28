package com.app.microservicio.usuarios.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class AuthenticationResponseDTO {
    private String message;

    public AuthenticationResponseDTO(String message) {
        this.message = message;
    }
}
