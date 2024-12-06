package com.app.microservicio.usuarios.controllers;

import com.app.microservicio.usuarios.DTO.UsuarioDTO;
import com.app.microservicio.usuarios.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) {
        Optional<UsuarioDTO> usuario = usuarioService.obtenerUsuario(id);
        return usuario.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        if (usuarioService.checkIfUsuarioExist(usuarioDTO.getNombreUsuario())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        UsuarioDTO nuevoUsuario = usuarioService.registrarUsuario(usuarioDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @GetMapping("/existe/{nombreUsuario}")
    public ResponseEntity<Boolean> checkIfUsuarioExist(@PathVariable String nombreUsuario) {
        boolean existe = usuarioService.checkIfUsuarioExist(nombreUsuario);
        return new ResponseEntity<>(existe, HttpStatus.OK);
    }

    //TODO AL CAMBIAR LA CONTRASEÑA DE UN USUARIO DEJA DE PODER REALIZAR CAMBIOS EN LOS ENDPOINTS
    @PutMapping("/{id}/cambiarContrasena")
    public ResponseEntity<Void> cambiarContraseñaUsuario(@PathVariable Long id, @RequestBody String nuevaContrasena) {
        boolean actualizado = usuarioService.cambiarContraseñaUsuario(id, nuevaContrasena);
        if (actualizado) {
            SecurityContextHolder.clearContext(); // Limpia la sesión actual
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}