package com.example.trabajofinanazas.Repositorio;

import com.example.trabajofinanazas.Entites.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Rusuario extends JpaRepository<Usuario, Integer> {
    
    // Buscar usuario por email
    Optional<Usuario> findByEmail(String email);
    
    // Verificar si existe un usuario con el email
    boolean existsByEmail(String email);
    
    // Buscar usuarios por nombre
    @Query("SELECT u FROM Usuario u WHERE u.nombre LIKE %:nombre%")
    java.util.List<Usuario> findByNombreContaining(@Param("nombre") String nombre);
}
