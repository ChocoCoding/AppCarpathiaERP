package com.app.microservicio.usuarios.repository;

import com.app.microservicio.usuarios.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre.
     *
     * @param nombreRol El nombre del rol.
     * @return Un Optional que contiene el rol si se encuentra, o vacío si no se encuentra.
     */
    Optional<Rol> findByNombreRol(String nombreRol);
}
