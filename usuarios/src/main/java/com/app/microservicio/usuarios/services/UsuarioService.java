package com.app.microservicio.usuarios.services;

import com.app.microservicio.usuarios.DTO.UsuarioDTO;
import com.app.microservicio.usuarios.config.CustomUserDetails;
import com.app.microservicio.usuarios.entities.Rol;
import com.app.microservicio.usuarios.entities.Usuario;
import com.app.microservicio.usuarios.repository.RolRepository;
import com.app.microservicio.usuarios.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;


    public List<UsuarioDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> obtenerUsuario(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(this::convertirADTO);
    }

    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getContrasena() == null || usuarioDTO.getContrasena().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }

        Usuario usuario = convertirAEntidad(usuarioDTO);
        usuario.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
        usuario.setEnabled(true);

        Set<Rol> roles = usuarioDTO.getRoles().stream()
                .map(nombreRol -> rolRepository.findByNombreRol(nombreRol)
                        .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombreRol)))
                .collect(Collectors.toSet());

        usuario.setRoles(roles);
        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return convertirADTO(nuevoUsuario);
    }

    public boolean checkIfUsuarioExist(String nombreUsuario) {
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    //TODO AL CAMBIAR LA CONTRASEÑA DE UN USUARIO DEJA DE PODER REALIZAR CAMBIOS EN LOS ENDPOINTS
    public boolean cambiarContraseñaUsuario(Long id, String nuevaContrasena) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }


    @Transactional
    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Eliminar las relaciones en la tabla usuario_roles
            entityManager.createNativeQuery("DELETE FROM usuario_roles WHERE id_usuario = :idUsuario")
                    .setParameter("idUsuario", id)
                    .executeUpdate();

            // Ahora eliminar el usuario
            usuarioRepository.delete(usuario);
            return true;
        }
        return false;
    }

    // Métodos de conversión entre Entidad y DTO
    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setNombreUsuario(usuario.getNombreUsuario());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setNombreCompleto(usuario.getNombreCompleto());
        usuarioDTO.setEnabled(usuario.isEnabled());
        usuarioDTO.setRoles(usuario.getRoles().stream()
                .map(rol -> rol.getNombreRol())
                .collect(Collectors.toSet()));
        return usuarioDTO;
    }

    private Usuario convertirAEntidad(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(usuarioDTO.getNombreUsuario());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuario.setEnabled(usuarioDTO.isEnabled());
        return usuario;
    }

}
