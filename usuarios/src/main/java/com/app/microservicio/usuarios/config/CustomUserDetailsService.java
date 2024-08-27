package com.app.microservicio.usuarios.config;

import com.app.microservicio.usuarios.entities.Usuario;
import com.app.microservicio.usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(usuario.getNombreUsuario(), usuario.getContrasena(),
                usuario.getRoles().stream()
                        .map(rol -> new org.springframework.security.core.authority.SimpleGrantedAuthority(rol.getNombreRol()))
                        .collect(Collectors.toList()));
    }
}
