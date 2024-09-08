package com.app.microservicio.usuarios.controllers;

import com.app.microservicio.usuarios.DTO.AuthenticationRequestDTO;
import com.app.microservicio.usuarios.DTO.AuthenticationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(@RequestBody AuthenticationRequestDTO authRequest) {
        System.out.println("Received authentication request for username: " + authRequest.getUsername());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getContrasena())
        );
        // Retorna una respuesta exitosa
        return ResponseEntity.ok(new AuthenticationResponseDTO("Autenticación exitosa"));
    }
}
